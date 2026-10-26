package M2fst.Guide_Michelin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EvaluationPhotoPutDto {
    private String url;

    public static EvaluationPhotoPutDto fromEntity(String url) {
        EvaluationPhotoPutDto rep = new EvaluationPhotoPutDto();
        rep.setUrl(url);
        return rep;
    }
}