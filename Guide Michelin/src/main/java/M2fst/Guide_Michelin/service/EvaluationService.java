package M2fst.Guide_Michelin.service;

import M2fst.Guide_Michelin.dto.EvaluationDto;
import M2fst.Guide_Michelin.dto.EvaluationPhotoPutDto;
import M2fst.Guide_Michelin.entity.EvaluationEntity;
import M2fst.Guide_Michelin.entity.RestaurantEntity;
import M2fst.Guide_Michelin.repository.EvaluationRepository;
import M2fst.Guide_Michelin.repository.RestaurantRepository;
import M2fst.Guide_Michelin.repository.S3Repository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;
    private final IndexService indexService;
    private final S3Repository s3Repository;

    public EvaluationDto addEvaluation(Integer restaurantId, String auteur, EvaluationDto evaluationDto) {
        RestaurantEntity restaurant;
        try {
            restaurant = this.restaurantRepository.findById(restaurantId).get();
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Restaurant introuvable : " + restaurantId);
        }
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setAuthor(auteur);
        evaluation.setContent(evaluationDto.getContent());
        evaluation.setNote(evaluationDto.getNote());
        evaluation.setPhotos(evaluationDto.getPhotos());
        evaluation.setRestaurant(restaurant);
        EvaluationEntity saved = this.evaluationRepository.save(evaluation);
        this.indexService.indexEvaluation(restaurantId, saved.getId(), saved.getContent());
        return this.toDto(saved);
    }

    public EvaluationDto updateEvaluation(Integer evaluationId, EvaluationDto evaluationDto, String auteur, boolean isAdmin) {
        try {
            EvaluationEntity evaluation = this.evaluationRepository.findById(evaluationId).get();
            this.checkOwnerOrAdmin(evaluation, auteur, isAdmin);
            evaluation.setContent(evaluationDto.getContent());
            evaluation.setNote(evaluationDto.getNote());
            evaluation.setPhotos(evaluationDto.getPhotos());
            EvaluationEntity saved = this.evaluationRepository.save(evaluation);
            this.indexService.deleteEvaluation(evaluationId);
            this.indexService.indexEvaluation(saved.getRestaurant().getId(), saved.getId(), saved.getContent());
            return this.toDto(saved);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Evaluation introuvable : " + evaluationId);
        }
    }

    public void deleteEvaluation(Integer evaluationId, String auteur, boolean isAdmin) {
        try {
            EvaluationEntity evaluation = this.evaluationRepository.findById(evaluationId).get();
            this.checkOwnerOrAdmin(evaluation, auteur, isAdmin);
            this.evaluationRepository.delete(evaluation);
            this.indexService.deleteEvaluation(evaluationId);
        } catch (NoSuchElementException e) {
            throw new EntityNotFoundException("Evaluation introuvable : " + evaluationId);
        }
    }

    public List<EvaluationDto> searchEvaluations(String motsCles) {
        List<EvaluationDto> resultats = new ArrayList<>();
        List<Integer> ids = this.indexService.searchEvaluations(motsCles);
        List<EvaluationEntity> evaluations = this.evaluationRepository.findAllById(ids);
        for (EvaluationEntity evaluation : evaluations) {
            resultats.add(this.toDto(evaluation));
        }
        return resultats;
    }

    public List<EvaluationDto> getEvaluationsByAuthor(String auteur) {
        List<EvaluationDto> resultats = new ArrayList<>();
        List<EvaluationEntity> evaluations = this.evaluationRepository.findByAuthor(auteur);
        for (EvaluationEntity evaluation : evaluations) {
            resultats.add(this.toDto(evaluation));
        }
        return resultats;
    }

    private void checkOwnerOrAdmin(EvaluationEntity evaluation, String auteur, boolean isAdmin) {
        if (!isAdmin && !evaluation.getAuthor().equals(auteur)) {
            throw new AccessDeniedException("Vous ne pouvez modifier que vos propres évaluations");
        }
    }

    private EvaluationDto toDto(EvaluationEntity evaluation) {
        EvaluationDto dto = new EvaluationDto().fromEntity(evaluation);
        List<String> urls = new ArrayList<>();
        for (String key : evaluation.getPhotos()) {
            urls.add(this.s3Repository.getEvaluationUrl(key));
        }
        dto.setPhotos(urls);
        return dto;
    }

    @Transactional
    public EvaluationPhotoPutDto uploadPhoto(Integer evaluationId, MultipartFile file, String auteur, boolean isAdmin) {
        EvaluationEntity evaluation = this.evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation introuvable : " + evaluationId));
        this.checkOwnerOrAdmin(evaluation, auteur, isAdmin);
        String key = "evaluation-" + evaluationId + "-" + UUID.randomUUID();
        try {
            s3Repository.uploadEvaluationImage(key, file);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de la photo", e);
        }
        evaluation.getPhotos().add(key);
        this.evaluationRepository.save(evaluation);
        return EvaluationPhotoPutDto.fromEntity(this.s3Repository.getEvaluationUrl(key));
    }
}