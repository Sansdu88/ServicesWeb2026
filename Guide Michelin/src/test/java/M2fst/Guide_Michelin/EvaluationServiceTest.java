package M2fst.Guide_Michelin;

import M2fst.Guide_Michelin.dto.EvaluationDto;
import M2fst.Guide_Michelin.entity.EvaluationEntity;
import M2fst.Guide_Michelin.entity.RestaurantEntity;
import M2fst.Guide_Michelin.repository.EvaluationRepository;
import M2fst.Guide_Michelin.repository.RestaurantRepository;
import M2fst.Guide_Michelin.service.EvaluationService;
import M2fst.Guide_Michelin.service.IndexService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EvaluationServiceTest {

    @InjectMocks
    private EvaluationService evaluationService;

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private IndexService indexService;

    @Test
    void ajouterEvaluation() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1);
        EvaluationDto dto = new EvaluationDto();
        dto.setContent("Très bon");
        dto.setNote(3);
        dto.setPhotos(new ArrayList<>());
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(evaluationRepository.save(any(EvaluationEntity.class))).thenAnswer(i -> i.getArgument(0));
        EvaluationDto resultat = evaluationService.addEvaluation(1, "gabriel", dto);
        assertEquals("gabriel", resultat.getAuthor());
        assertEquals("Très bon", resultat.getContent());
    }

    @Test
    void modifierEvaluation() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(1);
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setAuthor("gabriel");
        evaluation.setRestaurant(restaurant);
        EvaluationDto dto = new EvaluationDto();
        dto.setContent("Finalement moyen");
        dto.setNote(1);
        dto.setPhotos(new ArrayList<>());
        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluation));
        when(evaluationRepository.save(evaluation)).thenReturn(evaluation);
        EvaluationDto resultat = evaluationService.updateEvaluation(1, dto, "gabriel", false);
        assertEquals("Finalement moyen", resultat.getContent());
        assertEquals(1, resultat.getNote());
    }

    @Test
    void chercherEvaluation() {
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setContent("Très bon");
        when(indexService.searchEvaluations("bon")).thenReturn(List.of(1));
        when(evaluationRepository.findAllById(List.of(1))).thenReturn(List.of(evaluation));
        List<EvaluationDto> resultat = evaluationService.searchEvaluations("bon");
        assertEquals(1, resultat.size());
    }

    @Test
    void supprimerSonEvaluation() {
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setAuthor("gabriel");
        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluation));
        evaluationService.deleteEvaluation(1, "gabriel", false);
        verify(evaluationRepository).delete(evaluation);
    }

    @Test
    void supprimerEvaluationDunAutre() {
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setAuthor("gabriel");
        when(evaluationRepository.findById(1)).thenReturn(Optional.of(evaluation));
        assertThrows(AccessDeniedException.class, () -> evaluationService.deleteEvaluation(1, "toto", false));
    }
}