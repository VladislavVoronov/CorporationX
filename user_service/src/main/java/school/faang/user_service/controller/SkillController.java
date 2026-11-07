package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createSkill(@RequestBody @Valid SkillDto skillDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(skillService.createSkill(skillDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<String>> getUserSkills(@PathVariable Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(skillService.getUserSkills(userId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<SkillDto> deleteSkill(@RequestBody @Valid SkillDto skillDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(skillService.deleteSkill(skillDto));
    }

    @PutMapping("/update/{newTitle}")
    public ResponseEntity<String> updateSkill(@RequestBody @Valid SkillDto skillDto, @PathVariable @NotBlank @Size(max = 64) String newTitle) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(skillService.updateSkill(skillDto, newTitle));
    }
}
