package com.example.goldenticket2.service;

import com.example.goldenticket2.dto.AuthResponse;
import com.example.goldenticket2.dto.LoginRequest;
import com.example.goldenticket2.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
