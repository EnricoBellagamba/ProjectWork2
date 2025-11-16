package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.auth.LoginRequest;
import com.example.ProjectWork.dto.auth.LoginResponse;
import com.example.ProjectWork.dto.auth.RegisterRequest;

public interface AuthService {
    LoginResponse register(RegisterRequest req);
    LoginResponse login(LoginRequest req);
}
