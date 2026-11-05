package school.faang.user_service.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.*;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@Validated
@RequestMapping("/mentorship")
@RestController
@RequiredArgsConstructor
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @PostMapping("/create/{menteeId}")
    public ResponseEntity<MentorshipDto> createMentorship(@PathVariable long menteeId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mentorshipService.createMentorship(menteeId));
    }

    @GetMapping("/mentors/{userId}")
    public ResponseEntity<List<UserDtoForPublic>> getMentors(@PathVariable long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mentorshipService.getMentors(userId));
    }

    @GetMapping("/mentees/{userId}")
    public ResponseEntity<List<UserDtoForPublic>> getMentee(@PathVariable long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mentorshipService.getMentee(userId));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteMentorship(@PathVariable long userId) {
        mentorshipService.deleteMentorship(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/request/create/{userId}")
    public ResponseEntity<Long> createMentorshipRequest(@PathVariable long userId,
                                                        @RequestBody @NotBlank @Size(max = 4096) String description) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mentorshipService.createMentorshipRequest(userId, description));
    }

    @PostMapping("/request/accept/{mentorshipRequestId}")
    public ResponseEntity<MentorshipRequestAcceptDto> acceptMentorshipRequest(@PathVariable long mentorshipRequestId) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(mentorshipService.acceptMentorshipRequest(mentorshipRequestId));
    }

    @PutMapping("/request/reject/{mentorshipRequestId}")
    public ResponseEntity<MentorshipRequestRejectDto> rejectMentorshipRequest(@PathVariable long mentorshipRequestId,
                                                              @RequestBody @NotBlank @Size(max = 4096) String rejectReason) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mentorshipService.rejectMentorshipRequest(mentorshipRequestId, rejectReason));
    }
}