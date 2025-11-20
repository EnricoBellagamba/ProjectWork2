package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.auth.LoginRequest;
import com.example.ProjectWork.dto.auth.LoginResponse;
import com.example.ProjectWork.dto.auth.RegisterRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthService {
    LoginResponse register(RegisterRequest req, MultipartFile cvFile) throws IOException;
    LoginResponse login(LoginRequest req);
}
