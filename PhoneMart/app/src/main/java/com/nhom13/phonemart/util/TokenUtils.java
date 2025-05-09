package com.nhom13.phonemart.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nhom13.phonemart.api.AuthAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenUtils {
    private static AuthAPI authAPI;

    public static String getAccessToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getString("access_token", null);
    }

    public static String getRefreshToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        return prefs.getString("refresh_token", null);
    }

    public static void clearAllAuthData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static void saveTokens(Context context, String accessToken, String refreshToken) {
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .apply();
    }

    public static void createNewAccessToken(String refreshToken, TokenCallback callback) {
        authAPI = RetrofitClient.getClient().create(AuthAPI.class);
        authAPI.createNewAccessToken(refreshToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    JwtResponse jwtResponse = gson.fromJson(json, JwtResponse.class);

                    callback.onSuccess(jwtResponse);
                } else if (response.code() == 401) {
                    callback.onFailure("Please log in again");
                } else {
                    try {
                        String error = response.errorBody().string();
                        callback.onFailure(error);
                    } catch (IOException e) {
                        callback.onFailure("Lỗi đọc errorBody");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

}
