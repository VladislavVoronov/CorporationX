package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.exception.MentorshipNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserRepositoryService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final UserRepositoryService userRepositoryService;
    private final UserContext userContext;
    private final UserMapper userMapper;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    @Value("${user-service.mentorship.delay-months}")
    private long months;

    @Override
    public MentorshipDto createMentorship(long menteeId) {
        User mentor = userRepositoryService.findById(userContext.getUserId());
        User mentee = userRepositoryService.findById(menteeId);

        if (userContext.getUserId() == menteeId) {
            throw new DataValidationException("Нельзя связать менторством самого себя, ID: %s".formatted(menteeId));
        }

        if (mentor.getMentees().contains(mentee) || mentee.getMentors().contains(mentor)) {
            throw new DataValidationException("Данные пользователи уже связаны менторством. ID ментора: %s, ID менти: %s ."
                    .formatted(mentor.getId(), menteeId));
        }

        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);

        userRepositoryService.save(mentor);
        mentee = userRepositoryService.save(mentee);
        log.info("Ментор и менти успешно пересохранены в базу данных c созданной менторской связью. ID ментора: {}, ID менти: {}."
                , mentor.getId(), menteeId);

        return MentorshipDto.builder()
                .mentorId(userContext.getUserId())
                .menteeId(mentee.getId())
                .build();
    }

    @Override
    public List<UserDtoForPublic> getMentors(long userId) {
        return userRepositoryService.findById(userId).getMentors().stream()
                .map(userMapper::mapToUserDtoForPublic)
                .toList();
    }

    @Override
    public List<UserDtoForPublic> getMentee(long userId) {
        return userRepositoryService.findById(userId).getMentees().stream()
                .map(userMapper::mapToUserDtoForPublic)
                .toList();
    }

    @Override
    public void deleteMentorship(long userId) {

        User currentUser = userRepositoryService.findById(userContext.getUserId());
        User targetUser = userRepositoryService.findById(userId);

        if (targetUser.getMentees().contains(currentUser) || targetUser.getMentors().contains(currentUser)) {
            targetUser.getMentees().remove(currentUser);
            targetUser.getMentors().remove(currentUser);
            currentUser.getMentees().remove(targetUser);
            currentUser.getMentors().remove(targetUser);

            userRepositoryService.save(currentUser);
            userRepositoryService.save(targetUser);

            log.info("Связь между ментором и менти успешно удалена. ID ментора: {}, ID менти: {}."
                    , currentUser.getId(), targetUser.getId());

        } else {
            throw new DataValidationException(("Между пользователями нет менторской связи! " +
                    "ID текущего пользователя: %s, ID запрашиваемого пользователя: %s")
                    .formatted(currentUser.getId(), targetUser.getId()));
        }
    }

    @Override
    public long createMentorshipRequest(long userId, String description) {
        if (userId == userContext.getUserId()) {
            throw new DataValidationException("Нельзя направить запрос на менторство самому себе! ID текущего пользователя: %s"
                    .formatted(userContext.getUserId()));
        } else {
            User receiver = userRepositoryService.findById(userId);

            mentorshipRequestRepository.findLatestRequest(userContext.getUserId(), receiver.getId()).ifPresent(
                    (lastRequest) -> {
                        LocalDateTime availableAfter = lastRequest.getCreatedAt().plusMonths(months);
                        LocalDateTime now = LocalDateTime.now();
                        if (!now.isAfter(availableAfter)) {
                            throw new ForbiddenException(
                                    "С даты последнего запроса должно пройти более " + months + " месяцев. ID запроса: %s ID ментора: %s, ID менти: %s."
                                            .formatted(lastRequest.getId(), lastRequest.getReceiver(), lastRequest.getRequester()));
                        }
                    }
            );

            User requester = userRepositoryService.findById(userContext.getUserId());

            MentorshipRequest mentorshipRequest = MentorshipRequest.builder()
                    .description(description)
                    .requester(requester)
                    .receiver(receiver)
                    .status(RequestStatus.PENDING)
                    .build();

            mentorshipRequest = mentorshipRequestRepository.save(mentorshipRequest);
            log.info("Запрос на менторство успешно создан. ID ментора: {}, ID менти: {}."
                    , receiver.getId(), requester.getId());

            return mentorshipRequest.getId();
        }
    }

    @Override
    public MentorshipRequestAcceptDto acceptMentorshipRequest(long mentorshipRequestId) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(mentorshipRequestId);

        if (mentorshipRequest.getStatus() != RequestStatus.PENDING) {
            throw new ForbiddenException("Можно принять запрос только в статусе 'Ожидает'. ID запроса: %s"
                    .formatted(mentorshipRequest.getId()));
        }

        if (mentorshipRequest.getReceiver().getId() != userContext.getUserId()) {
            throw new ForbiddenException(("Можно принять только тот запрос, который адресован текущему пользователю." +
                    "ID запроса: %s, ID: текущего пользователя: %s.")
                    .formatted(mentorshipRequest.getId(), userContext.getUserId()));
        }

        createMentorship(mentorshipRequest.getRequester().getId());

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequest = mentorshipRequestRepository.save(mentorshipRequest);
        log.info("Запрос на менторство успешно принят ментором. ID ментора: {}, ID менти: {}."
                , userContext.getUserId(), mentorshipRequest.getRequester().getId());

        return MentorshipRequestAcceptDto.builder()
                .requestStatus(RequestStatus.ACCEPTED)
                .mentorshipRequestId(mentorshipRequest.getId())
                .build();
    }

    @Override
    public MentorshipRequestRejectDto rejectMentorshipRequest(long mentorshipRequestId, String rejectReason) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(mentorshipRequestId);

        if (mentorshipRequest.getStatus() != RequestStatus.PENDING) {
            throw new ForbiddenException("Можно принять запрос только в статусе 'Ожидает'. ID запроса: %s"
                    .formatted(mentorshipRequest.getId()));
        }

        if (mentorshipRequest.getReceiver().getId() != userContext.getUserId()) {
            throw new ForbiddenException(("Можно отклонить только тот запрос, который адресован текущему пользователю." +
                    "ID запроса: %s, ID: текущего пользователя: %s.")
                    .formatted(mentorshipRequest.getId(), userContext.getUserId()));
        }

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectReason);
        mentorshipRequest = mentorshipRequestRepository.save(mentorshipRequest);
        log.info("Запрос на менторство отклонён ментором. ID ментора: {}, ID менти: {}."
                , userContext.getUserId(), mentorshipRequest.getRequester().getId());

        return MentorshipRequestRejectDto.builder()
                .rejectReason(rejectReason)
                .requestStatus(mentorshipRequest.getStatus())
                .mentorshipRequestId(mentorshipRequest.getId())
                .build();
    }

    private MentorshipRequest findMentorshipRequestById(long mentorshipRequestId) {
        return mentorshipRequestRepository.findById(mentorshipRequestId).orElseThrow(
                () -> new MentorshipNotFoundException("Запрос на менторство не существует! ID запроса: %s."
                        .formatted(mentorshipRequestId)));
    }
}