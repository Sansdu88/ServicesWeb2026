package M2fst.Guide_Michelin;

import M2fst.Guide_Michelin.dto.RestaurantDto;
import M2fst.Guide_Michelin.entity.EvaluationEntity;
import M2fst.Guide_Michelin.entity.RestaurantEntity;
import M2fst.Guide_Michelin.repository.RestaurantRepository;
import M2fst.Guide_Michelin.service.IndexService;
import M2fst.Guide_Michelin.service.RestaurantService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private IndexService indexService;

    @Test
    void ajouterRestaurant() {
        when(restaurantRepository.save(any(RestaurantEntity.class))).thenAnswer(i -> i.getArgument(0));
        RestaurantDto resultat = restaurantService.addRestaurant("Chez Paul", "1 rue de Paris");
        assertEquals("Chez Paul", resultat.getNom());
        assertEquals("1 rue de Paris", resultat.getAdresse());
    }

    @Test
    void listerRestaurants() {
        when(restaurantRepository.findAll()).thenReturn(List.of(new RestaurantEntity(), new RestaurantEntity()));
        List<RestaurantDto> resultat = restaurantService.getRestaurants();
        assertEquals(2, resultat.size());
    }

    @Test
    void restaurantSansEvaluation() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setNom("Chez Paul");
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        RestaurantDto resultat = restaurantService.getRestaurant(1);
        assertEquals(-1.0, resultat.getMoyenne());
    }

    @Test
    void restaurantAvecEvaluations() {
        RestaurantEntity restaurant = new RestaurantEntity();
        EvaluationEntity eval1 = new EvaluationEntity();
        eval1.setNote(1);
        EvaluationEntity eval2 = new EvaluationEntity();
        eval2.setNote(3);
        restaurant.getEvaluations().add(eval1);
        restaurant.getEvaluations().add(eval2);
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        RestaurantDto resultat = restaurantService.getRestaurant(1);
        assertEquals(2.0, resultat.getMoyenne());
    }

    @Test
    void restaurantIntrouvable() {
        when(restaurantRepository.findById(99)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> restaurantService.getRestaurant(99));
    }

    @Test
    void modifierRestaurant() {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setNom("Ancien nom");
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);
        RestaurantDto resultat = restaurantService.updateRestaurant(1, "Nouveau nom", "1 rue de Paris");
        assertEquals("Nouveau nom", resultat.getNom());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void supprimerRestaurant() {
        RestaurantEntity restaurant = new RestaurantEntity();
        EvaluationEntity evaluation = new EvaluationEntity();
        evaluation.setId(5);
        restaurant.getEvaluations().add(evaluation);
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        restaurantService.deleteRestaurant(1);
        verify(indexService).deleteEvaluation(5);
        verify(restaurantRepository).delete(restaurant);
    }
}