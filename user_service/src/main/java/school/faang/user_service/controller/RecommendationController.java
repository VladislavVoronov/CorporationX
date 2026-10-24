package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.RecommendationDtoForUpdate;
import school.faang.user_service.dto.RecommendationRequestAcceptDto;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.RecommendationService;

@RequiredArgsConstructor
@RequestMapping("/recommendations")
@RestController
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/create")
    public RecommendationDto createRecommendation(@RequestBody @Valid RecommendationDto recommendationDto){
        return recommendationService.createRecommendation(recommendationDto);
    }

    @DeleteMapping("/delete/{recommendationId}")
    public long deleteRecommendation(@PathVariable long recommendationId){
        return recommendationService.deleteRecommendation(recommendationId);
    }

    @PutMapping("/update")
    public RecommendationDtoForUpdate updateRecommendation(@RequestBody @Valid RecommendationDtoForUpdate recommendationDtoForUpdate){
        return recommendationService.updateRecommendation(recommendationDtoForUpdate);
    }

    @PostMapping("/request")
    public long createRecommendationRequest(@RequestBody @Valid RecommendationRequestDto recommendationRequestDto){
        return recommendationService.createRecommendationRequest(recommendationRequestDto);
    }

    @PostMapping("/accept/{recommendationRequestId}")
    public RecommendationRequestAcceptDto acceptRecommendationRequest(@PathVariable long recommendationRequestId,
                                                                      @RequestBody @NotBlank @Size(max = 4096) String content){
        return recommendationService.acceptRecommendationRequest(recommendationRequestId, content);
    }

    @PutMapping("/reject/{recommendationRequestId}")
    public String rejectRecommendationRequest(@PathVariable long recommendationRequestId, @RequestBody @NotBlank @Size(max = 4096) String rejectionReason){
        return recommendationService.rejectRecommendationRequest(recommendationRequestId, rejectionReason);
    }

    @GetMapping("/{recommendationRequestId}")
    public RecommendationRequestDto getRecommendationRequestById(@PathVariable long recommendationRequestId){
        return recommendationService.getRecommendationRequest(recommendationRequestId);
    }
}
