package M2fst.Guide_Michelin.repository;


import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;


@Repository
public class S3Repository {

    private static final String BUCKET_RESTAURANT = "restaurant";
    private static final String BUCKET_EVALUATION = "evaluation";
    private static final Duration URL_DURATION = Duration.ofMinutes(10);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3Repository(@Value("${s3.url}") String url,
                        @Value("${s3.access-key}") String accessKey,
                        @Value("${s3.secret-key}") String secretKey) {
        StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey));
        S3Configuration pathStyle = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentials)
                .endpointOverride(URI.create(url))
                .serviceConfiguration(pathStyle)
                .build();

        this.s3Presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentials)
                .endpointOverride(URI.create(url))
                .serviceConfiguration(pathStyle)
                .build();
    }

    public void uploadRestaurantImage(String key, MultipartFile file) throws IOException {
        this.upload(BUCKET_RESTAURANT, key, file);
    }

    public String getRestaurantUrl(String key) {
        return this.presign(BUCKET_RESTAURANT, key);
    }

    public void uploadEvaluationImage(String key, MultipartFile file) throws IOException {
        this.upload(BUCKET_EVALUATION, key, file);
    }

    public String getEvaluationUrl(String key) {
        return this.presign(BUCKET_EVALUATION, key);
    }

    private void upload(String bucket, String key, MultipartFile file) throws IOException {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();
        this.s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    private String presign(String bucket, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(URL_DURATION)
                .getObjectRequest(request)
                .build();
        return this.s3Presigner.presignGetObject(presignRequest).url().toExternalForm();
    }

    @PreDestroy
    public void close() {
        this.s3Client.close();
        this.s3Presigner.close();
    }
}