package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserRepositoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final UserRepositoryService userRepositoryService;
    private final UserContext userContext;
    private final UserMapper userMapper;

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
}
