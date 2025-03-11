package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.dtos.UpdateUserDto;
import com.liushukov.courseFlow.models.User;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Optional;

public interface UserService {

    public User getUserDetails(String username);

    User getUserFromAuthentication(Authentication authentication);

    Optional<User> getUserById(Long id);

    List<User> getAllUsers(String orderBy, String sortBy, int pageNumber, int pageSize);

    User saveManager(RegisterDto registerDto);

    User updateUser(User user, UpdateUserDto userDto);

    void deleteUser(User user);
}
