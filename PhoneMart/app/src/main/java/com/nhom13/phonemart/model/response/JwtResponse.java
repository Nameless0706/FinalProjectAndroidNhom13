package com.nhom13.phonemart.model.response;

import com.nhom13.phonemart.dto.UserDto;

public class JwtResponse {
    private UserDto user;
    private String accessToken;
    private String refreshToken;

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
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
}
