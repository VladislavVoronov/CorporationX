package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RequestMapping("/skills")
@RestController
@RequiredArgsConstructor
@Validated
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/create")
    public String createSkill(@RequestBody @Valid SkillDto skillDto){
        return skillService.createSkill(skillDto);
    }

    @GetMapping("/{userId}")
    public List<String> getUserSkills(@PathVariable Long userId){
        return skillService.getUserSkills(userId);
    }

    @DeleteMapping("/delete")
    public SkillDto deleteSkill(@RequestBody @Valid SkillDto skillDto){
        return skillService.deleteSkill(skillDto);
    }

    @PutMapping("/update/{newTitle}")
    public String updateSkill(@RequestBody @Valid SkillDto skillDto, @PathVariable String newTitle){
        return skillService.updateSkill(skillDto, newTitle);
    }
}
