package M2fst.Guide_Michelin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Evaluation")
public class EvaluationEntity {
    @Id
    @Positive
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "author", length = 50, nullable = false)
    private String author;

    @Column(name = "content", length = 255)
    private String content;

    @Min(0)
    @Max(3)
    @Column(name = "note", nullable = false)
    private Integer note;

    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "evaluation_photo", joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "url_photo")
    private List<String> photos = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurant", nullable = false)
    private RestaurantEntity restaurant;
}