package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.dtos.UpdateUserDto;
import com.liushukov.courseFlow.models.Role;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.UserRepository;
import com.liushukov.courseFlow.services.UserService;
import com.liushukov.courseFlow.services.VerificationAccountService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           VerificationAccountService verificationAccountService,
                           @Lazy UserService userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Cacheable(value = "userCache", key = "#username")
    public User getUserDetails(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUserFromAuthentication(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            return userService.getUserDetails(username);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User retrieval exception", exception);
        }
    }

    @Cacheable(value = "userCache", key = "#id")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers(String orderBy, String sortBy, int pageNumber, int pageSize) {
        Pageable pageable;
        switch (orderBy) {
            case "desc" -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
            default -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        }
        return userRepository.findAll(pageable).getContent();
    }

    @Transactional
    public User saveManager(RegisterDto registerDto) {
        var manager = new User()
                .setFullName(registerDto.fullName())
                .setRole(Role.MANAGER)
                .setEmail(registerDto.email())
                .setPassword(passwordEncoder.encode(registerDto.password()));
        return userRepository.save(manager);
    }

    @CacheEvict(value = "userCache", key = "#user.email")
    @Transactional
    public User updateUser(User user, UpdateUserDto userDto) {
        if (userDto.fullName() != null) {
            user.setFullName(userDto.fullName());
        }
        if (userDto.password() != null) {
            user.setPassword(passwordEncoder.encode(userDto.password()));
        }
        return userRepository.save(user);
    }

    @CacheEvict(value = "userCache", key = "#user.id")
    @Transactional
    public void deleteUser(User user) {
        user.setEnabled(false);
        userRepository.save(user);
    }
}
