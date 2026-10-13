package school.faang.user_service.service;

import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.RecommendationDtoForUpdate;

public interface RecommendationService {

    RecommendationDto createRecommendation(RecommendationDto recommendationDto);
    long deleteRecommendation(long recommendationId);
    RecommendationDtoForUpdate updateRecommendation(RecommendationDtoForUpdate recommendationDtoForUpdate);
}
