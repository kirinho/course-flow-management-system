package com.liushukov.courseFlow.oauth2.controllers;

import com.liushukov.courseFlow.oauth2.dto.LoginDto;
import com.liushukov.courseFlow.oauth2.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class MainController {
    private final AuthenticationService authenticationService;

    public MainController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login-oauth2")
    public ResponseEntity<String> authentication(@RequestBody LoginDto loginDto) {
        if (loginDto.fullName() == null || loginDto.email() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String jwt = authenticationService.authenticate(loginDto);
        return ResponseEntity.status(HttpStatus.OK).body(jwt);
    }
}
