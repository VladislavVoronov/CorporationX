package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserDtoForUpdate;
import school.faang.user_service.service.UserService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public UserDto createUser(@RequestBody @Valid UserDto userDto){
       return userService.createUser(userDto);
    }

    @PutMapping("/update/{userId}")
    public UserDto updateUser(@RequestBody @Valid UserDtoForUpdate userDtoForUpdate, @PathVariable long userId){
        return userService.updateUser(userDtoForUpdate, userId);
    }
    @DeleteMapping("/delete/{userId}")
    public UserDto deleteUser(@PathVariable long userId){
        return userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId){
        return userService.getUser(userId);
    }
}

