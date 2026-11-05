package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.RecommendationDtoForUpdate;
import school.faang.user_service.dto.RecommendationRequestAcceptDto;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationService;

@RequiredArgsConstructor
@RequestMapping("/recommendations")
@RestController
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/create")
    public ResponseEntity<RecommendationDto> createRecommendation(@RequestBody @Valid RecommendationDto recommendationDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(recommendationService.createRecommendation(recommendationDto));
    }

    @DeleteMapping("/delete/{recommendationId}")
    public ResponseEntity<Long> deleteRecommendation(@PathVariable long recommendationId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(recommendationService.deleteRecommendation(recommendationId));
    }

    @PutMapping("/update")
    public ResponseEntity<RecommendationDtoForUpdate> updateRecommendation(@RequestBody @Valid RecommendationDtoForUpdate recommendationDtoForUpdate) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(recommendationService.updateRecommendation(recommendationDtoForUpdate));
    }

    @PostMapping("/request")
    public ResponseEntity<Long> createRecommendationRequest(@RequestBody @Valid RecommendationRequestDto recommendationRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(recommendationService.createRecommendationRequest(recommendationRequestDto));
    }

    @PostMapping("/accept/{recommendationRequestId}")
    public ResponseEntity<RecommendationRequestAcceptDto> acceptRecommendationRequest(@PathVariable long recommendationRequestId, @RequestBody @NotBlank @Size(max = 4096) String content) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(recommendationService.acceptRecommendationRequest(recommendationRequestId, content));
    }

    @PutMapping("/reject/{recommendationRequestId}")
    public ResponseEntity<String> rejectRecommendationRequest(@PathVariable long recommendationRequestId, @RequestBody @NotBlank @Size(max = 4096) String rejectionReason) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(recommendationService.rejectRecommendationRequest(recommendationRequestId, rejectionReason));
    }

    @GetMapping("/{recommendationRequestId}")
    public ResponseEntity<RecommendationRequestDto> getRecommendationRequestById(@PathVariable long recommendationRequestId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(recommendationService.getRecommendationRequest(recommendationRequestId));
    }
}
