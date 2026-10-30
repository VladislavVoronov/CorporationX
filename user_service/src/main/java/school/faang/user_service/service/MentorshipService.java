package school.faang.user_service.service;

import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface MentorshipService {

    MentorshipDto createMentorship(long menteeId);

    List<UserDto> getMentors(long userId);

    List<UserDto> getMentee(long userId);

    void deleteMentorship(long userId);
}
