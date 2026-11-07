package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userDto));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserDtoForUpdate userDtoForUpdate, @PathVariable long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUser(userDtoForUpdate, userId));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.deleteUser(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUser(userId));
    }
}

