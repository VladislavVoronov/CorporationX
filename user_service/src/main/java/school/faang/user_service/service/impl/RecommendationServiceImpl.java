package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.RecommendationDtoForUpdate;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.service.UserRepositoryService;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final UserRepositoryService userRepositoryService;
    private final RecommendationMapper recommendationMapper;

    @Override
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        User author = userRepositoryService.findById(recommendationDto.getAuthorId());
        User receiver = userRepositoryService.findById(recommendationDto.getReceiverId());

        Recommendation recommendation = recommendationMapper.mapToRecommendation(recommendationDto);
        recommendation = recommendationRepository.save(recommendation);

        author.getRecommendationsGiven().add(recommendation);
        receiver.getRecommendationsReceived().add(recommendation);
        userRepository.save(author);
        userRepository.save(receiver);

        return recommendationMapper.mapToRecommendationDto(recommendation);
    }

    @Override
    public long deleteRecommendation(long recommendationId) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId).orElseThrow(
                () -> new EntityNotFoundException("Recommendation not found")
        );
        User author = recommendation.getAuthor();
        author.getRecommendationsGiven().remove(recommendation);
        User receiver = recommendation.getReceiver();
        receiver.getRecommendationsReceived().remove(recommendation);
        userRepository.save(author);
        userRepository.save(receiver);

        recommendationRepository.deleteById(recommendationId);

        return recommendationId;
    }

    @Override
    public RecommendationDtoForUpdate updateRecommendation(RecommendationDtoForUpdate recommendationDtoForUpdate){
        Recommendation recommendation = recommendationRepository.findById(recommendationDtoForUpdate.getRecommendationId()).orElseThrow(
                ()-> new EntityNotFoundException("recommendation not found")
        );
        recommendation.setContent(recommendationDtoForUpdate.getContent());
        recommendation = recommendationRepository.save(recommendation);
        recommendationDtoForUpdate.setContent(recommendation.getContent());
        recommendationDtoForUpdate.setRecommendationId(recommendation.getId());

        return recommendationDtoForUpdate;
    }
}