package com.example.goldenticket2.config;

import com.example.goldenticket2.entity.Role;
import com.example.goldenticket2.entity.RoleName;
import com.example.goldenticket2.entity.User;
import com.example.goldenticket2.repository.RoleRepository;
import com.example.goldenticket2.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            for (RoleName roleName : RoleName.values()) {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
            }

            if (!userRepository.existsByEmail("admin@goldenticket.com")) {
                Role admin = roleRepository.findByName(RoleName.ADMIN).orElseThrow();
                userRepository.save(User.builder()
                        .fullName("GoldenTicket Admin")
                        .email("admin@goldenticket.com")
                        .password(passwordEncoder.encode("Admin123*"))
                        .roles(Set.of(admin))
                        .enabled(true)
                        .build());
            }
        };
    }
}
