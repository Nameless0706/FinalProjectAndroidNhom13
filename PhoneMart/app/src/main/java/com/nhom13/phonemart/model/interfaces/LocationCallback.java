package com.nhom13.phonemart.model.interfaces;

public interface LocationCallback {
    void onLocationReceived(double latitude, double longitude);
    void onError(String message);
}
