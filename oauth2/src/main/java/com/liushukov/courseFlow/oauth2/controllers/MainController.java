package com.liushukov.courseFlow.oauth2.controllers;

import com.liushukov.courseFlow.oauth2.dto.LoginDto;
import com.liushukov.courseFlow.oauth2.services.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class MainController {

    private final AuthenticationService authenticationService;

    public MainController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/login-oauth2")
    public ResponseEntity<String> authentication(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("given_name");
        String surname = oAuth2User.getAttribute("family_name");
        Boolean emailVerified = oAuth2User.getAttribute("email_verified");

        LoginDto loginDto = new LoginDto(name, surname, email);
        return (Boolean.TRUE.equals(emailVerified))
                ? ResponseEntity.status(HttpStatus.OK).body(authenticationService.authenticate(loginDto))
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Your account isn't valid:(");
    }
}
