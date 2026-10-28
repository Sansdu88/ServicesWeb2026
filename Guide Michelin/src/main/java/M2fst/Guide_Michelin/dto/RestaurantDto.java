package M2fst.Guide_Michelin.dto;

import M2fst.Guide_Michelin.entity.EvaluationEntity;
import M2fst.Guide_Michelin.entity.RestaurantEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantDto {
    public Integer id;
    @NotBlank
    @Size(max = 90)
    public String nom;
    @NotBlank
    @Size(max = 255)
    public String adresse;
    public String url_image_restaurant;
    public Double moyenne;
    public List<EvaluationDto> evaluations;

    public static RestaurantDto fromEntity(RestaurantEntity restaurantEntity){
        RestaurantDto restaurantDto = new RestaurantDto();
        restaurantDto.id = restaurantEntity.getId();
        restaurantDto.nom = restaurantEntity.getNom();
        restaurantDto.adresse = restaurantEntity.getAdresse();
        restaurantDto.url_image_restaurant = restaurantEntity.getUrlImageRestaurant();
        restaurantDto.evaluations = new ArrayList<>();

        List<EvaluationEntity> evaluations = restaurantEntity.getEvaluations();
        if (evaluations == null || evaluations.isEmpty()) {
            restaurantDto.moyenne = (double) -1;
        } else {
            double somme = 0;
            for (EvaluationEntity evaluation : evaluations) {
                somme += evaluation.getNote();
                restaurantDto.evaluations.add(EvaluationDto.fromEntity(evaluation));
            }
            restaurantDto.moyenne = somme / evaluations.size();
        }
        return restaurantDto;
    }
}