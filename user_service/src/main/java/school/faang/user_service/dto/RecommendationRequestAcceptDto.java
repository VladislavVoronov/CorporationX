package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Builder
@Data
public class RecommendationRequestAcceptDto {

    private RecommendationDto recommendationDto;
    private RequestStatus requestStatus;
}
