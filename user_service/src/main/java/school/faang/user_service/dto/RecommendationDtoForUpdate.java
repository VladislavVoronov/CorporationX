package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RecommendationDtoForUpdate {

    private long recommendationId;

    @NotBlank
    @Size(max = 4096)
    private String content;

}
