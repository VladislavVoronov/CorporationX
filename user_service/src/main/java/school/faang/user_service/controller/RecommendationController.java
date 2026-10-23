package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.RecommendationDtoForUpdate;
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
}
