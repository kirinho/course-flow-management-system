package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.LoginDto;
import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.dtos.LoginOauth2Dto;
import com.liushukov.courseFlow.models.Role;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterDto input) {
        var user = new User()
                .setName(input.name())
                .setSurname(input.surname())
                .setRole(Role.STUDENT)
                .setEmail(input.email())
                .setEnabled(false)
                .setPassword(passwordEncoder.encode(input.password()));

        return userRepository.save(user);
    }

    public User authenticate(LoginDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.email(),
                        input.password()
                )
        );

        return userRepository.findByEmail(input.email()).orElseThrow();
    }

    public User signupOauth2(LoginOauth2Dto input) {
        var user = new User()
                .setName(input.name())
                .setSurname(input.surname())
                .setRole(Role.STUDENT)
                .setEmail(input.email())
                .setEnabled(true);

        return userRepository.save(user);
    }
}
