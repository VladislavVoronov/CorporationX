package school.faang.user_service.service;

import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.RecommendationDtoForUpdate;
import school.faang.user_service.dto.RecommendationRequestAcceptDto;
import school.faang.user_service.dto.RecommendationRequestDto;

public interface RecommendationService {

    RecommendationDto createRecommendation(RecommendationDto recommendationDto);
    long deleteRecommendation(long recommendationId);
    RecommendationDtoForUpdate updateRecommendation(RecommendationDtoForUpdate recommendationDtoForUpdate);
    long createRecommendationRequest(RecommendationRequestDto recommendationRequestDto);
    String rejectRecommendationRequest(long recommendationRequestId, String rejectionReason);
    RecommendationRequestAcceptDto acceptRecommendationRequest(long recommendationRequestId, String content);
    RecommendationRequestDto getRecommendationRequest(long recommendationRequestId);
}
