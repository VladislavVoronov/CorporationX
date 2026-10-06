package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RequestMapping("/skills")
@RestController
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/create")
    public String createSkill(@RequestBody SkillDto skillDto){
        return skillService.createSkill(skillDto);
    }

    @GetMapping("/{userId}")
    public List<String> getUserSkills(@PathVariable Long userId){
        return skillService.getUserSkills(userId);
    }

}
