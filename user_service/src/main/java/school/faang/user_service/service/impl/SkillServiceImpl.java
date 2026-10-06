package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.SkillService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Override
    public String createSkill(SkillDto skillDto) {
        Skill skill = new Skill();
        skill.setTitle(skillDto.getTitle());
        User user = userRepository.findByUsername(skillDto.getUsername()).orElseThrow(
                ()-> new EntityNotFoundException("User not found")
        );
        List<User> userList = new ArrayList<>();
        userList.add(user);
        skill.setUsers(userList);
        skill = skillRepository.save(skill);
        user.getSkills().add(skill);
        userRepository.save(user);
        return skill.getTitle();
    }
}
