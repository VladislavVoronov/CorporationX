package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.dto.MentorshipRequestAcceptDto;
import school.faang.user_service.dto.MentorshipRequestRejectDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserRepositoryService;

import java.time.LocalDateTime;
import java.util.List;

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
            throw new RuntimeException("Нельзя связать менторством самого себя");
        }

        if (mentor.getMentees().contains(mentee) || mentee.getMentors().contains(mentor)) {
            throw new RuntimeException("Данные пользователи уже связаны менторством");
        }

        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);

        userRepositoryService.save(mentor);
        mentee = userRepositoryService.save(mentee);

        return MentorshipDto.builder()
                .mentorId(userContext.getUserId())
                .menteeId(mentee.getId())
                .build();
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        return userRepositoryService.findById(userId).getMentors().stream()
                .map(userMapper::mapToUserDto)
                .toList();
    }

    @Override
    public List<UserDto> getMentee(long userId) {
        return userRepositoryService.findById(userId).getMentees().stream()
                .map(userMapper::mapToUserDto)
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

        } else {
            throw new RuntimeException("Между пользователя нет менторской связи");
        }
    }

    @Override
    public long createMentorshipRequest(long userId, String description) {
        if (userId == userContext.getUserId()) {
            throw new RuntimeException("Нельзя направить запрос на менторство самому себе");
        } else {
            User receiver = userRepositoryService.findById(userId);

            mentorshipRequestRepository.findLatestRequest(userContext.getUserId(), receiver.getId()).ifPresent(
                    (lastRequest) -> {
                        LocalDateTime availableAfter = lastRequest.getCreatedAt().plusMonths(months);
                        LocalDateTime now = LocalDateTime.now();
                        if (!now.isAfter(availableAfter)) {
                            throw new IllegalStateException(
                                    "С даты последнего запроса должно пройти более " + months + " месяцев"
                            );
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

            return mentorshipRequest.getId();
        }
    }

    @Override
    public MentorshipRequestAcceptDto acceptMentorshipRequest(long mentorshipRequestId) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(mentorshipRequestId);

        if (mentorshipRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Можно принять запрос только в статусе 'Ожидает'");
        }

        if (mentorshipRequest.getReceiver().getId() != userContext.getUserId()) {
            throw new IllegalArgumentException("Можно принять только тот запрос, который адресован текущему пользователю");
        }

        createMentorship(mentorshipRequest.getRequester().getId());

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequest = mentorshipRequestRepository.save(mentorshipRequest);

        return MentorshipRequestAcceptDto.builder()
                .requestStatus(RequestStatus.ACCEPTED)
                .mentorshipRequestId(mentorshipRequest.getId())
                .build();
    }

    @Override
    public MentorshipRequestRejectDto rejectMentorshipRequest(long mentorshipRequestId, String rejectReason) {
        MentorshipRequest mentorshipRequest = findMentorshipRequestById(mentorshipRequestId);

        if (mentorshipRequest.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Можно принять запрос только в статусе 'Ожидает'");
        }

        if (mentorshipRequest.getReceiver().getId() != userContext.getUserId()) {
            throw new IllegalArgumentException("Можно отклонить только тот запрос, который адресован текущему пользователю");
        }

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectReason);
        mentorshipRequest = mentorshipRequestRepository.save(mentorshipRequest);

        return MentorshipRequestRejectDto.builder()
                .rejectReason(rejectReason)
                .requestStatus(mentorshipRequest.getStatus())
                .mentorshipRequestId(mentorshipRequest.getId())
                .build();
    }

    private MentorshipRequest findMentorshipRequestById(long mentorshipRequestId) {
        return mentorshipRequestRepository.findById(mentorshipRequestId).orElseThrow(
                () -> new EntityNotFoundException("Запрос на менторство не существует.")
        );
    }
}