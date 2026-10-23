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
import school.faang.user_service.service.UserRepositoryService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final UserRepositoryService userRepositoryService;

    private static final String MESSAGE_SKILL_NOT_FOUND = "Skill not found";

    @Override
    public String createSkill(SkillDto skillDto) {
        Skill skill = new Skill();
        skill.setTitle(skillDto.getTitle());

        User user = userRepositoryService.findUserByUsername(skillDto.getUsername());
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
        User user = userRepositoryService.findById(userId);
        List<String> userSkills = new ArrayList<>();
        for (Skill skill : user.getSkills()) {
            userSkills.add(skill.getTitle());
        }

        return userSkills;
    }

    @Override
    public SkillDto deleteSkill(SkillDto skillDto) {
        User user = userRepositoryService.findUserByUsername(skillDto.getUsername());

        List<Skill> userSkills = user.getSkills();
        Skill skill = findSkillByTitle(skillDto.getTitle());
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
        User user = userRepositoryService.findUserByUsername(skillDto.getUsername());
        Skill skill = findSkillByTitle(skillDto.getTitle());
        List<Skill> userSkills = user.getSkills();

        userSkills.remove(skill);
        skill.setTitle(newTitle);
        skill = skillRepository.save(skill);

        userSkills.add(skill);
        user.setSkills(userSkills);
        userRepository.save(user);

        return skill.getTitle();
    }

    private Skill findSkillByTitle(String title) {
        return skillRepository.findByTitle(title).orElseThrow(
                () -> new EntityNotFoundException(MESSAGE_SKILL_NOT_FOUND)
        );
    }
}
