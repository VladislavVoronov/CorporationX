package school.faang.user_service.service;


import school.faang.user_service.dto.SkillDto;

import java.util.List;

public interface SkillService {

   String createSkill(SkillDto skillDto);
   List<String> getUserSkills(Long userId);
   SkillDto deleteSkill(SkillDto skillDto);
   String updateSkill(SkillDto skillDto, String newTitle);
}


