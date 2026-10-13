package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.RecommendationDtoForUpdate;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.RecommendationService;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    @Override
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        Recommendation recommendation = new Recommendation();
        User author = userRepository.findById(recommendationDto.getAuthorId()).orElseThrow(
                () -> new EntityNotFoundException("Author not found")
        );
        User receiver = userRepository.findById(recommendationDto.getReceiverId()).orElseThrow(
                () -> new EntityNotFoundException("Receiver not found")
        );
        recommendation.setContent(recommendationDto.getContent());
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation = recommendationRepository.save(recommendation);
        author.getRecommendationsGiven().add(recommendation);
        receiver.getRecommendationsReceived().add(recommendation);
        userRepository.save(author);
        userRepository.save(receiver);
        recommendationDto.setAuthorId(author.getId());
        recommendationDto.setReceiverId(receiver.getId());
        recommendationDto.setContent(recommendation.getContent());
        return recommendationDto;
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
