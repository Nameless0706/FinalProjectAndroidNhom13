package com.nhom13.phonemart.model.interfaces;

import com.nhom13.phonemart.model.response.JwtResponse;

public interface TokenCallback {
    void onSuccess(JwtResponse jwtResponse);
    void onFailure(String errorMessage);
}
