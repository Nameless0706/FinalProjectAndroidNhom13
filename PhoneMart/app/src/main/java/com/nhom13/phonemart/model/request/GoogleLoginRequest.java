package com.nhom13.phonemart.model.request;

public class GoogleLoginRequest {
    private String idToken;

    public GoogleLoginRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }
}

