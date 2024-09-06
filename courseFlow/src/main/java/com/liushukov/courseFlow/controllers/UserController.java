package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.dtos.UpdateUserDto;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.services.UserService;
import com.liushukov.courseFlow.services.VerificationAccountService;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> me(Authentication authentication) {
        var response = userService.getUserFromAuthentication(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<User>> allUsers() {
        var response = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/create-manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createManager(@Valid @RequestBody RegisterDto registerDto) {
        var response = userService.saveManager(registerDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/update-account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateUser(Authentication authentication, @Valid @RequestBody UpdateUserDto userDto) {
        var user = userService.getUserFromAuthentication(authentication);
        var response = userService.updateUser(user, userDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete-account")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteUser(Authentication authentication) {
        var user = userService.getUserFromAuthentication(authentication);
        userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
