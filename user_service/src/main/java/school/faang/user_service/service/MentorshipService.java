package school.faang.user_service.service;

import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.user.User;

import java.util.List;

public interface MentorshipService {

    MentorshipDto createMentorship(MentorshipDto mentorshipDto);
    List<UserDto> getMentors(long userId);
}
