package M2fst.Guide_Michelin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantImagePutDto {
    private String url;

    public static RestaurantImagePutDto fromEntity(String url){
        RestaurantImagePutDto rep = new RestaurantImagePutDto();
        rep.setUrl(url);
        return rep;
    }
}
