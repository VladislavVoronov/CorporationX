package school.faang.user_service.service;

import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.dto.MentorshipRequestAcceptDto;
import school.faang.user_service.dto.MentorshipRequestRejectDto;
import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface MentorshipService {

    MentorshipDto createMentorship(long menteeId);

    List<UserDto> getMentors(long userId);

    List<UserDto> getMentee(long userId);

    void deleteMentorship(long userId);

    long createMentorshipRequest(long userId, String description);

    MentorshipRequestAcceptDto acceptMentorshipRequest(long mentorshipRequestId);

    MentorshipRequestRejectDto rejectMentorshipRequest(long mentorshipRequestId, String rejectReason);
}
