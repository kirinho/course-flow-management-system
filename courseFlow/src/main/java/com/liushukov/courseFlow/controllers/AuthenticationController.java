package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.LoginDto;
import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.dtos.LoginOauth2Dto;
import com.liushukov.courseFlow.exceptions.CustomException;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.UserRepository;
import com.liushukov.courseFlow.services.AuthenticationService;
import com.liushukov.courseFlow.services.JwtService;
import com.liushukov.courseFlow.services.VerificationAccountService;
import com.liushukov.courseFlow.services.impl.VerificationAccountServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    private final UserRepository userRepository;

    private final VerificationAccountService verificationAccountService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserRepository userRepository, VerificationAccountServiceImpl verificationAccountService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.verificationAccountService = verificationAccountService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterDto registerUserDto) throws CustomException {
        var user = userRepository.findByEmail(registerUserDto.email());
        if (user.isEmpty()) {
            User registeredUser = authenticationService.signup(registerUserDto);
            verificationAccountService.sendVerificationEmail(registeredUser);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@Valid @RequestBody LoginDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
    }

    @PostMapping("/login-oauth2")
    public ResponseEntity<String> authenticateOauth2(@Valid @RequestBody LoginOauth2Dto loginOauth2Dto) {
        User user = userRepository.findByEmail(loginOauth2Dto.email())
                .orElseGet(() -> authenticationService.signupOauth2(loginOauth2Dto));

        String jwtToken = jwtService.generateToken(user);
        return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
    }

    @PostMapping("/verify-account")
    public ResponseEntity<Void> verifyAccount(@RequestParam(value = "email") String email) throws CustomException {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (!user.get().isEnabled()) {
                verificationAccountService.sendVerificationEmail(user.get());
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestParam(value = "token") String token) throws CustomException {
        try {
            var user = verificationAccountService.confirmEmail(token);
            String jwtToken = jwtService.generateToken(user);
            return ResponseEntity.status(HttpStatus.OK).body(jwtToken);
        } catch (Exception exception){
            if (exception instanceof CustomException){
                throw exception;
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
