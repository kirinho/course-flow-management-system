package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.dtos.UpdateUserDto;
import com.liushukov.courseFlow.models.Role;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserDetails(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUserFromAuthentication(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            return getUserDetails(username);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User retrieval exception", exception);
        }
    }

    public List<User> getAllUsers() {;
        return userRepository.findAll();
    }

    public User saveManager(RegisterDto registerDto) {
        var manager = new User()
                .setName(registerDto.name())
                .setSurname(registerDto.surname())
                .setRole(Role.MANAGER)
                .setEmail(registerDto.email())
                .setPassword(passwordEncoder.encode(registerDto.password()));
        return userRepository.save(manager);
    }

    public User updateUser(User user, UpdateUserDto userDto) {
        if (userDto.name() != null) {
            user.setName(userDto.name());
        }
        if (userDto.surname() != null) {
            user.setSurname(userDto.surname());
        }
        if (userDto.email() != null) {
            user.setEmail(userDto.email());
        }
        if (userDto.password() != null) {
            user.setPassword(passwordEncoder.encode(userDto.password()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        user.setEnabled(false);
    }
}
