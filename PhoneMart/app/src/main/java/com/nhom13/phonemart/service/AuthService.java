package com.nhom13.phonemart.service;

import android.content.Context;
import android.util.Log;

import com.nhom13.phonemart.api.AuthAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.util.TokenUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthService {
    private AuthAPI authAPI;
    private Context context;

    public AuthService(Context context) {
        this.context = context;

        authAPI = RetrofitClient.getClient().create(AuthAPI.class);
    }

    public void logout() {
        String refreshToken = TokenUtils.getRefreshToken(context);

        if (refreshToken == null) {
            // Token không tồn tại => có thể đã đăng xuất
            return;
        }

        authAPI.logout(refreshToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Xóa token khỏi SharedPreferences
                    TokenUtils.clearAllAuthData(context);
                } else {
                    // Xử lý lỗi từ phía server (ví dụ token không còn tồn tại)
                    try {
                        Log.d("error", "Server error: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.d("error", "onResponse: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Xử lý lỗi kết nối (timeout, no internet,...)
                Log.d("error", "API call failed: " + t.getMessage());
            }
        });
    }
}
