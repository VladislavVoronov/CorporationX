package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RecommendationDto {

    @NotBlank
    @Size(max = 4096)
    private String content;

    private long authorId;
    private long receiverId;

}
