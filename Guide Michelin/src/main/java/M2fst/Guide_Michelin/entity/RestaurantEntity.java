package M2fst.Guide_Michelin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Restaurant")
public class RestaurantEntity {
    @Id
    @Positive
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nom", length = 90, nullable = false)
    private String nom;

    @Column(name = "adresse", length = 255, nullable = false)
    private String adresse;

    @Column(name = "url_image_restaurant")
    private String urlImageRestaurant;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationEntity> evaluations = new ArrayList<>();
}