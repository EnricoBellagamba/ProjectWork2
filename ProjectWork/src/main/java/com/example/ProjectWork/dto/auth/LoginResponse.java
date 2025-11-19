package com.example.ProjectWork.dto.auth;


import com.example.ProjectWork.dto.UtenteDto;

public class LoginResponse{
private String accessToken;
private String refreshToken;
private UtenteDto user;

public LoginResponse(String accessToken, String refreshToken, UtenteDto user) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.user = user;
}

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UtenteDto getUser() {
        return user;
    }

    public void setUser(UtenteDto user) {
        this.user = user;
    }
}