package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.RecommendationDtoForUpdate;
import school.faang.user_service.dto.RecommendationRequestAcceptDto;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.exception.RecommendationRequestNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.service.UserRepositoryService;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final UserRepositoryService userRepositoryService;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserContext userContext;

    @Value("${user-service.recommendation.delay-months}")
    private long months;

    @Override
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        Recommendation recommendation = prepareForCreateRecommendation(recommendationDto);

        return recommendationMapper.mapToRecommendationDto(recommendation);
    }

    @Override
    public long deleteRecommendation(long recommendationId) {
        Recommendation recommendation = findRecommendation(recommendationId);
        User author = recommendation.getAuthor();
        author.getRecommendationsGiven().remove(recommendation);
        User receiver = recommendation.getReceiver();
        receiver.getRecommendationsReceived().remove(recommendation);
        userRepository.save(author);
        userRepository.save(receiver);

        recommendationRepository.deleteById(recommendationId);
        log.info("Рекоммендация успешно удалена. ID рекоммендации: {}", recommendationId);

        return recommendationId;
    }

    @Override
    public RecommendationDtoForUpdate updateRecommendation(RecommendationDtoForUpdate recommendationDtoForUpdate) {
        Recommendation recommendation = findRecommendation(recommendationDtoForUpdate.getRecommendationId());
        recommendation.setContent(recommendationDtoForUpdate.getContent());
        recommendation = recommendationRepository.save(recommendation);
        log.info("Рекоммендация успешно обновлена. ID рекоммендации: {}", recommendation.getId());

        recommendationDtoForUpdate.setContent(recommendation.getContent());
        recommendationDtoForUpdate.setRecommendationId(recommendation.getId());

        return recommendationDtoForUpdate;
    }

    @Override
    public long createRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestRepository.findLatestPendingRequest(
                userContext.getUserId(),
                recommendationRequestDto.getReceiverId()).ifPresent(lastRequest -> {
                    LocalDateTime availableAfter = lastRequest.getCreatedAt().plusMonths(months);
                    LocalDateTime now = LocalDateTime.now();
                    if (!now.isAfter(availableAfter)) {
                        throw new ForbiddenException(
                                "С даты создания должно пройти более %s месяцев. ID запроса: %s, ID ментора: %s, ID менти: %s."
                                        .formatted(months, lastRequest.getId(), lastRequest.getReceiver().getId(), lastRequest.getRequester().getId()));
                    }
                }
        );

        RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                .requester(userRepositoryService.findById(userContext.getUserId()))
                .receiver(userRepositoryService.findById(recommendationRequestDto.getReceiverId()))
                .message(recommendationRequestDto.getMessage())
                .status(RequestStatus.PENDING)
                .build();

        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        log.info("Запрос рекоммендации успешно создан. ID запроса: {}", recommendationRequest.getId());

        return recommendationRequest.getId();
    }

    @Override
    public String rejectRecommendationRequest(long recommendationRequestId, String rejectionReason) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(recommendationRequestId).orElseThrow(
                () -> new RecommendationRequestNotFoundException("Запрос на рекоммендацию не найден! Запрашиваемый ID: %s, ID текущего пользователя: %s."
                        .formatted(recommendationRequestId, userContext.getUserId())));

        validateActionsUser(recommendationRequest);

        recommendationRequest.setRejectionReason(rejectionReason);
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        log.info("Запрос на рекоммендацию отклонён. ID запроса: {}", recommendationRequest.getId());

        return recommendationRequest.getRejectionReason();
    }

    @Override
    public RecommendationRequestAcceptDto acceptRecommendationRequest(long recommendationRequestId, String content) {
        RecommendationRequest recommendationRequest = findRecommendationRequestById(recommendationRequestId);

        validateActionsUser(recommendationRequest);

        RecommendationDto recommendationDto = RecommendationDto.builder()
                .authorId(recommendationRequest.getReceiver().getId())
                .receiverId(recommendationRequest.getRequester().getId())
                .content(content)
                .build();

        Recommendation recommendation = prepareForCreateRecommendation(recommendationDto);

        recommendationRequest.setRecommendation(recommendation);
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        log.info("Запрос на рекоммендацию успешно принят. ID запроса: {}. ID принявшего пользователя: {}"
                , recommendationRequest.getId(), recommendationRequest.getReceiver().getId());

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

    private RecommendationRequest findRecommendationRequestById(long recommendationRequestId) {
        return recommendationRequestRepository.findById(recommendationRequestId).orElseThrow(
                () -> new RecommendationRequestNotFoundException("Запрос на рекоммендацию не найден! Запрашиваемый ID: %s, ID текущего пользователя: %s."
                        .formatted(recommendationRequestId, userContext.getUserId())));
    }

    private Recommendation prepareForCreateRecommendation(RecommendationDto recommendationDto) {
        User author = userRepositoryService.findById(recommendationDto.getAuthorId());
        User receiver = userRepositoryService.findById(recommendationDto.getReceiverId());

        Recommendation recommendation = recommendationMapper.mapToRecommendation(recommendationDto);
        recommendation = recommendationRepository.save(recommendation);

        author.getRecommendationsGiven().add(recommendation);
        receiver.getRecommendationsReceived().add(recommendation);
        userRepository.save(author);
        userRepository.save(receiver);
        log.info("Рекоммендация успешно создана. ID Рекоммендации: {}, ID получателя: {}, ID отправителя: {}.",
                recommendation.getId(), receiver.getId(), author.getId());

        return recommendation;
    }

    private void validateActionsUser(RecommendationRequest recommendationRequest) {
        if (userContext.getUserId() != recommendationRequest.getReceiver().getId()) {
            throw new ForbiddenException(("Можно принять только тот запрос, который адресован текущему пользователю." +
                    "ID запроса: %s, ID: текущего пользователя: %s.")
                    .formatted(recommendationRequest.getId(), userContext.getUserId()));
        }

        if (recommendationRequest.getStatus() != RequestStatus.PENDING) {
            throw new ForbiddenException("Можно принять запрос только в статусе 'Ожидает'. ID запроса: %s"
                    .formatted(recommendationRequest.getId()));
        }
    }

    private Recommendation findRecommendation(long recommendationId) {
        return recommendationRepository.findById(recommendationId).orElseThrow(
                () -> new RecommendationRequestNotFoundException(("Запрос на рекоммендацию не найден! " +
                        "Запрашиваемый ID: %s, ID текущего пользователя: %s.")
                        .formatted(recommendationId, userContext.getUserId())));
    }
}