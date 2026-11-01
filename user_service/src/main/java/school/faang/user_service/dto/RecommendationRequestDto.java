package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.annotation.DifferentUsers;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@DifferentUsers
public class RecommendationRequestDto {

    private long requesterId;
    private long receiverId;

    @NotBlank
    @Size(max = 4096)
    private String message;
}
