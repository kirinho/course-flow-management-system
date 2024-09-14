package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.dtos.UpdateEmailDto;
import com.liushukov.courseFlow.dtos.UpdateUserDto;
import com.liushukov.courseFlow.exceptions.CustomException;
import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.UserRepository;
import com.liushukov.courseFlow.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> me(Authentication authentication) {
        var response = userService.getUserFromAuthentication(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<User>> allUsers(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String orderBy,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
            ) {
        SortingOrderEnum order = SortingOrderEnum.valueOf(orderBy.toUpperCase());
        var response = userService.getAllUsers(order, sortBy, pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/create-manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createManager(@Valid @RequestBody RegisterDto registerDto) {
        var manager = userRepository.findByEmail(registerDto.email());
        if (manager.isEmpty()) {
            var response = userService.saveManager(registerDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/update-account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateUser(Authentication authentication, @Valid @RequestBody UpdateUserDto userDto) {
        var user = userService.getUserFromAuthentication(authentication);
        var response = userService.updateUser(user, userDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/update-email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateUserEmail(Authentication authentication, @Valid @RequestBody UpdateEmailDto emailDto) throws CustomException {
        var user = userService.getUserFromAuthentication(authentication);
        var response = userService.updateEmail(user, emailDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete-account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteUser(Authentication authentication) {
        var user = userService.getUserFromAuthentication(authentication);
        if (user.isEnabled()) {
            userService.deleteUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("Account was successfully deleted!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Account was already deleted");
        }
    }

    @PatchMapping("/update-account-admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserForAdmin(@PathVariable Long userId, @Valid @RequestBody UpdateUserDto userDto) {
        var user = userService.getUserById(userId);
        return (user.isPresent())
                ? ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(user.get(), userDto))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/delete-account-admin/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserForAdmin(@PathVariable Long userId) {
        var user = userService.getUserById(userId);
        if (user.isPresent()) {
            if (user.get().isEnabled()) {
                userService.deleteUser(user.get());
                return ResponseEntity.status(HttpStatus.OK).body("Account was successfully deleted!");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Account was already deleted");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with given id wasn't find!");
        }
    }
}
