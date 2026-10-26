package M2fst.Guide_Michelin.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDto {
    private int code;
    private String message;
}