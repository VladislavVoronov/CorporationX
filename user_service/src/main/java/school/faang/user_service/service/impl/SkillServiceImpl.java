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
                () -> new EntityNotFoundException("User not found")
        );
        List<User> userList = new ArrayList<>();
        userList.add(user);
        skill.setUsers(userList);
        skill = skillRepository.save(skill);
        user.getSkills().add(skill);
        userRepository.save(user);
        return skill.getTitle();
    }


    @Override
    public List<String> getUserSkills(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        List<String> userSkills = new ArrayList<>();
        for (Skill skill : user.getSkills()) {
            userSkills.add(skill.getTitle());
        }
        return userSkills;
    }

    @Override
    public SkillDto deleteSkill(SkillDto skillDto) {
        User user = userRepository.findByUsername(skillDto.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        List<Skill> userSkills = user.getSkills();
        Skill skill = skillRepository.findByTitle(skillDto.getTitle()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        userSkills.remove(skill);
        user.setSkills(userSkills);
        userRepository.save(user);
        skillRepository.delete(skill);

        skillDto.setUsername(user.getUsername());
        skillDto.setTitle(skill.getTitle());

        return skillDto;

    }

    @Override
    public String updateSkill(SkillDto skillDto, String newTitle) {
        User user = userRepository.findByUsername(skillDto.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        Skill skill = skillRepository.findByTitle(skillDto.getTitle()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        List<Skill> userSkills = user.getSkills();
        userSkills.remove(skill);
        skill.setTitle(newTitle);
        skill = skillRepository.save(skill);
        userSkills.add(skill);
        user.setSkills(userSkills);
        userRepository.save(user);
        return skill.getTitle();



    }


}
