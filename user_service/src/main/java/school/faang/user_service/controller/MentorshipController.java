package school.faang.user_service.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.dto.MentorshipRequestAcceptDto;
import school.faang.user_service.dto.MentorshipRequestRejectDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Validated
@RequestMapping("/mentorship")
@RestController
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @PostMapping("/create/{menteeId}")
    public MentorshipDto createMentorship(@PathVariable long menteeId) {
        return mentorshipService.createMentorship(menteeId);
    }

    @GetMapping("/mentors/{userId}")
    public List<UserDto> getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }

    @GetMapping("/mentees/{userId}")
    public List<UserDto> getMentee(@PathVariable long userId) {
        return mentorshipService.getMentee(userId);
    }

    @DeleteMapping("/delete/{userId}")
    public void deleteMentorship(@PathVariable long userId) {
        mentorshipService.deleteMentorship(userId);
    }

    @PostMapping("/request/create/{userId}")
    public long createMentorshipRequest(@PathVariable long userId,
                                        @RequestBody @NotBlank @Size(max = 4096) String description) {
        return mentorshipService.createMentorshipRequest(userId, description);
    }

    @PostMapping("/request/accept/{mentorshipRequestId}")
    public MentorshipRequestAcceptDto acceptMentorshipRequest(@PathVariable long mentorshipRequestId) {
        return mentorshipService.acceptMentorshipRequest(mentorshipRequestId);
    }

    @PutMapping("/request/reject/{mentorshipRequestId}")
    public MentorshipRequestRejectDto rejectMentorshipRequest(@PathVariable long mentorshipRequestId,
                                                              @RequestBody String rejectReason) {
        return mentorshipService.rejectMentorshipRequest(mentorshipRequestId, rejectReason);
    }
}