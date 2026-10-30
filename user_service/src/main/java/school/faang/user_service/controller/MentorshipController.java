package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

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
}
