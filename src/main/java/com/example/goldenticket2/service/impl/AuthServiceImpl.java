package com.example.goldenticket2.service.impl;

import com.example.goldenticket2.dto.AuthResponse;
import com.example.goldenticket2.dto.LoginRequest;
import com.example.goldenticket2.dto.RegisterRequest;
import com.example.goldenticket2.entity.Role;
import com.example.goldenticket2.entity.RoleName;
import com.example.goldenticket2.entity.User;
import com.example.goldenticket2.exception.BadRequestException;
import com.example.goldenticket2.repository.RoleRepository;
import com.example.goldenticket2.repository.UserRepository;
import com.example.goldenticket2.security.JwtService;
import com.example.goldenticket2.security.SecurityUser;
import com.example.goldenticket2.service.AuthService;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already registered");
        }

        Set<RoleName> requestedRoles = request.roles() == null || request.roles().isEmpty()
                ? Set.of(RoleName.USER)
                : request.roles();
        Set<Role> roles = requestedRoles.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BadRequestException("Role not configured: " + roleName)))
                .collect(Collectors.toSet());

        User user = userRepository.save(User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(roles)
                .enabled(true)
                .build());
        return buildResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        Set<RoleName> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        String token = jwtService.generateToken(
                new SecurityUser(user),
                Map.of("roles", roleNames.stream().map(Enum::name).toList(), "userId", user.getId())
        );
        return new AuthResponse(user.getId(), user.getFullName(), user.getEmail(), roleNames, token, "Bearer");
    }
}