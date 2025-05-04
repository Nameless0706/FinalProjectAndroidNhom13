package com.nhom13.phonemart.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.api.UserAPI;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.util.TokenUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private UserAPI userAPI;
    private Context context;

    public UserService(Context context) {
        this.context = context;

        userAPI = RetrofitClient.getClient().create(UserAPI.class);
    }

    public void getUserDto(Long userId, GeneralCallBack<UserDto> callback) {
        String accessToken = TokenUtils.getAccessToken(context);

        userAPI.getUserById(userId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object rawData = response.body().getData();

                    // Đảm bảo ép kiểu đúng
                    Gson gson = new Gson();
                    String json = gson.toJson(rawData);
                    UserDto userDto = gson.fromJson(json, UserDto.class);

                    callback.onSuccess(userDto);
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            getUserDto(userId, callback);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            callback.onError(new Exception("Token refresh failed: " + errorMessage));
                        }
                    });
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        callback.onError(new Exception("API Error: " + errorBody));
                    } catch (IOException e) {
                        callback.onError(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }
}
