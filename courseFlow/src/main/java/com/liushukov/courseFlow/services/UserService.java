package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.dtos.UpdateEmailDto;
import com.liushukov.courseFlow.dtos.UpdateUserDto;
import com.liushukov.courseFlow.exceptions.CustomException;
import com.liushukov.courseFlow.models.Role;
import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.UserRepository;
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
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerificationAccountService verificationAccountService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationAccountService verificationAccountService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationAccountService = verificationAccountService;
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

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers(SortingOrderEnum order, String sortBy, int pageNumber, int pageSize) {
        Pageable pageable;
        switch (order) {
            case DESC -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
            default -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        }
        return userRepository.findAll(pageable).getContent();
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
        if (userDto.password() != null) {
            user.setPassword(passwordEncoder.encode(userDto.password()));
        }
        return userRepository.save(user);
    }

    public String updateEmail(User user, UpdateEmailDto emailDto) throws CustomException {
        user.setEmail(emailDto.email());
        user.setEnabled(false);
        userRepository.save(user);
        return verificationAccountService.sendVerificationEmail(user);
    }

    public void deleteUser(User user) {
        user.setEnabled(false);
        userRepository.save(user);
    }
}
