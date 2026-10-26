package M2fst.Guide_Michelin.service;

import M2fst.Guide_Michelin.dto.RestaurantDto;
import M2fst.Guide_Michelin.dto.RestaurantImagePutDto;
import M2fst.Guide_Michelin.entity.EvaluationEntity;
import M2fst.Guide_Michelin.entity.RestaurantEntity;
import M2fst.Guide_Michelin.repository.S3Repository;
import M2fst.Guide_Michelin.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final IndexService indexService;
    private final S3Repository s3Repository;

    public RestaurantDto addRestaurant(String titre, String adresse) {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setAdresse(adresse);
        restaurantEntity.setNom(titre);
        return this.toDto(this.restaurantRepository.save(restaurantEntity));
    }

    @Transactional(readOnly = true)
    public List<RestaurantDto> getRestaurants() {
        List<RestaurantDto> resultats = new ArrayList<>();
        for (RestaurantEntity restaurantEntity : this.restaurantRepository.findAll()) {
            resultats.add(this.toDto(restaurantEntity));
        }
        return resultats;
    }

    @Transactional(readOnly = true)
    public RestaurantDto getRestaurant(Integer restaurantId) {
        return this.toDto(this.findRestaurant(restaurantId));
    }

    @Transactional
    public RestaurantDto updateRestaurant(Integer restaurantId, String nom, String adresse) {
        RestaurantEntity restaurantEntity = this.findRestaurant(restaurantId);
        restaurantEntity.setNom(nom);
        restaurantEntity.setAdresse(adresse);
        return this.toDto(this.restaurantRepository.save(restaurantEntity));
    }

    @Transactional
    public void deleteRestaurant(Integer restaurantId) {
        RestaurantEntity restaurantEntity = this.findRestaurant(restaurantId);
        for (EvaluationEntity evaluation : restaurantEntity.getEvaluations()) {
            this.indexService.deleteEvaluation(evaluation.getId());
        }
        this.restaurantRepository.delete(restaurantEntity);
    }

    @Transactional
    public RestaurantImagePutDto uploadImage(Integer restaurantId, MultipartFile file) {
        RestaurantEntity restaurantEntity = this.findRestaurant(restaurantId);
        String key = "restaurant-" + restaurantId;
        try {
            s3Repository.uploadRestaurantImage(key, file);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
        restaurantEntity.setUrlImageRestaurant(key);
        this.restaurantRepository.save(restaurantEntity);
        return RestaurantImagePutDto.fromEntity(this.s3Repository.getRestaurantUrl(key));
    }

    private RestaurantEntity findRestaurant(Integer restaurantId) {
        try {
            return this.restaurantRepository.findById(restaurantId).get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Restaurant introuvable : " + restaurantId);
        }
    }

    private RestaurantDto toDto(RestaurantEntity restaurantEntity) {
        RestaurantDto dto = RestaurantDto.fromEntity(restaurantEntity);
        if (restaurantEntity.getUrlImageRestaurant() != null) {
            dto.url_image_restaurant = this.s3Repository.getRestaurantUrl(restaurantEntity.getUrlImageRestaurant());
        }
        return dto;
    }
}