package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.UserRepositoryService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MentorshipServiceImpl implements MentorshipService {

    private final UserRepositoryService userRepositoryService;
    private final UserContext userContext;
    private final UserMapper userMapper;

    @Override
    public MentorshipDto createMentorship(MentorshipDto mentorshipDto) {
        User mentor = userRepositoryService.findById(userContext.getUserId());
        User mentee = userRepositoryService.findById(mentorshipDto.getMenteeId());

        if(userContext.getUserId() == mentorshipDto.getMenteeId()){
            throw new RuntimeException("Нельзя связать менторством смого себя");
        }

        if (mentor.getMentees().contains(mentee)||mentee.getMentors().contains(mentor)) {
            throw new RuntimeException("Данные пользователи уже связаны менторством");
        }

        mentor.getMentees().add(mentee);
        mentee.getMentors().add(mentor);

        userRepositoryService.save(mentor);
        mentee = userRepositoryService.save(mentee);

        mentorshipDto = MentorshipDto.builder()
                .mentorId(userContext.getUserId())
                .menteeId(mentee.getId())
                .build();

        return mentorshipDto;
    }

    @Override
    public List<UserDto> getMentors(long userId) {
        return userRepositoryService.findById(userId).getMentors().stream()
                .map(userToDto-> userMapper.mapToUserDto(userToDto))
                .toList();
    }
}
