package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.dtos.UpdateUserDto;
import com.liushukov.courseFlow.exceptions.CustomException;
import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.models.User;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Optional;

public interface UserService {

    public User getUserDetails(String username);

    User getUserFromAuthentication(Authentication authentication);

    Optional<User> getUserById(Long id);

    List<User> getAllUsers(SortingOrderEnum order, String sortBy, int pageNumber, int pageSize);

    User saveManager(RegisterDto registerDto);

    User updateUser(User user, UpdateUserDto userDto);

    void deleteUser(User user);
}
