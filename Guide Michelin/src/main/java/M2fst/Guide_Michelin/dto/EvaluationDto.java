package M2fst.Guide_Michelin.dto;
import M2fst.Guide_Michelin.entity.EvaluationEntity;
import M2fst.Guide_Michelin.entity.RestaurantEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class EvaluationDto {
    public Integer id;
    public String author;
    @Size(max = 255)
    public String content;
    @NotNull
    @Min(0)
    @Max(3)
    public Integer note;
    @NotEmpty
    public List<String> photos;

    public static EvaluationDto fromEntity(EvaluationEntity evaluationEntity){
        EvaluationDto dto = new EvaluationDto();
        dto.id=evaluationEntity.getId();
        dto.author=evaluationEntity.getAuthor();
        dto.content=evaluationEntity.getContent();
        dto.note=evaluationEntity.getNote();
        dto.photos=evaluationEntity.getPhotos();
        return dto;
    }
}
