package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.service.SkillService;

@RequestMapping("/skills")
@RestController
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/create")
    public String createSkill(@RequestBody SkillDto skillDto){
        return skillService.createSkill(skillDto);
    }

}
