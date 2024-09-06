package com.liushukov.courseFlow.bootstrap;

import com.liushukov.courseFlow.dtos.RegisterDto;
import com.liushukov.courseFlow.models.Role;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    public AdminSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdministrator();
    }

    private void createSuperAdministrator() {
        RegisterDto userDto = new RegisterDto(
                "Admin",
                "Adminov",
                "admincourseflow@gmail.com",
                "87654321"
        );

        var existedAdmin = userRepository.findByEmail(userDto.email());
        if (existedAdmin.isEmpty()) {
            var user = new User()
                    .setName(userDto.name())
                    .setSurname(userDto.surname())
                    .setEmail(userDto.email())
                    .setPassword(passwordEncoder.encode(userDto.password()))
                    .setRole(Role.ADMIN)
                    .setEnabled(true);

            userRepository.save(user);
        }
    }
}
