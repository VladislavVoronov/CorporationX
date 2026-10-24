package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.RecommendationDtoForUpdate;
import school.faang.user_service.dto.RecommendationRequestAcceptDto;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
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
    private final RecommendationRequestRepository recommendationRequestRepository;

    @Override
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        Recommendation recommendation = prepareForCreateRecommendation(recommendationDto);

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

    @Override
    public long createRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {

        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .requester(userRepositoryService.findById(recommendationRequestDto.getRequesterId()))
                .receiver(userRepositoryService.findById(recommendationRequestDto.getReceiverId()))
                .message(recommendationRequestDto.getMessage())
                .status(RequestStatus.PENDING)
                .build();

        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequest.getId();
    }

    @Override
    public String rejectRecommendationRequest(long recommendationRequestId, String rejectionReason) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(recommendationRequestId).orElseThrow(
                ()-> new EntityNotFoundException("Recommendation Request not found")
        );

        recommendationRequest.setRejectionReason(rejectionReason);
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return recommendationRequest.getRejectionReason();
    }

    @Override
    public RecommendationRequestAcceptDto acceptRecommendationRequest(long recommendationRequestId, String content) {
        RecommendationRequest recommendationRequest = findRecommendationRequestById(recommendationRequestId);

        RecommendationDto recommendationDto = RecommendationDto.builder()
                .authorId(recommendationRequest.getRequester().getId())
                .receiverId(recommendationRequest.getReceiver().getId())
                .content(content)
                .build();

        Recommendation recommendation = prepareForCreateRecommendation(recommendationDto);

        recommendationRequest.setRecommendation(recommendation);
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return RecommendationRequestAcceptDto.builder()
                .recommendationDto(recommendationDto)
                .requestStatus(recommendationRequest.getStatus())
                .build();
    }

    @Override
    public RecommendationRequestDto getRecommendationRequest(long recommendationRequestId) {
        RecommendationRequest recommendationRequest = findRecommendationRequestById(recommendationRequestId);

        return RecommendationRequestDto.builder()
                .receiverId(recommendationRequest.getReceiver().getId())
                .requesterId(recommendationRequest.getRequester().getId())
                .message(recommendationRequest.getMessage())
                .build();
    }

    private RecommendationRequest findRecommendationRequestById(long recommendationRequestId){
        return recommendationRequestRepository.findById(recommendationRequestId).orElseThrow(
                ()-> new EntityNotFoundException("Recommendation Request not found")
        );
    }

    private Recommendation prepareForCreateRecommendation(RecommendationDto recommendationDto){
        User author = userRepositoryService.findById(recommendationDto.getAuthorId());
        User receiver = userRepositoryService.findById(recommendationDto.getReceiverId());

        Recommendation recommendation = recommendationMapper.mapToRecommendation(recommendationDto);
        recommendation = recommendationRepository.save(recommendation);

        author.getRecommendationsGiven().add(recommendation);
        receiver.getRecommendationsReceived().add(recommendation);
        userRepository.save(author);
        userRepository.save(receiver);

        return recommendation;
    }
}