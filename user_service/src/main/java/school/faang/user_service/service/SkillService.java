package school.faang.user_service.service;


import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.user.Skill;

import java.util.List;

public interface SkillService {

   String createSkill(SkillDto skillDto);
   List<String> getUserSkills(Long userId);

}


