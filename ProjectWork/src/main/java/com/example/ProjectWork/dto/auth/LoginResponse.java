package com.example.ProjectWork.dto.auth;




public class LoginResponse{
private String accessToken;
private String refreshToken;
private UtenteDto user;

public LoginResponse(String accessToken, String refreshToken, UtenteDto user) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.user = user;
}

// getter e setter
}