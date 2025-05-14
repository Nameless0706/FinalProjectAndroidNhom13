package com.nhom13.phonemart.service;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.api.UserAPI;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.request.UserPasswordUpdateRequest;
import com.nhom13.phonemart.model.request.UserUpdateRequest;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.TokenUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {
    private UserAPI userAPI;
    private Context context;
    private ImageService imageService;

    public UserService(Context context) {
        this.context = context;

        userAPI = RetrofitClient.getClient().create(UserAPI.class);
        imageService = new ImageService(context);
    }

    public void getUserDto(Long userId, GeneralCallBack<UserDto> callback) {
        String accessToken = TokenUtils.getAccessToken(context);

        if (TextUtils.isEmpty(accessToken)) {
            callback.onSuccess(null);
            return;
        }

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

    public void handleSaveFavoriteProduct(Long userId, Long productId, GeneralCallBack<UserDto> generalCallBack) {
        String accessToken = TokenUtils.getAccessToken(context);

        if (TextUtils.isEmpty(accessToken)) {
            generalCallBack.onSuccess(null);
            return;
        }

        userAPI.saveFavoriteProduct(userId, productId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object rawData = response.body().getData();

                    // Đảm bảo ép kiểu đúng
                    Gson gson = new Gson();
                    String json = gson.toJson(rawData);
                    UserDto userDto = gson.fromJson(json, UserDto.class);

                    generalCallBack.onSuccess(userDto);
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            handleSaveFavoriteProduct(userId, productId, generalCallBack);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            generalCallBack.onError(new Exception(errorMessage));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                generalCallBack.onError(throwable);
            }
        });
    }

    public void updateUserDetails(Long userId, UserUpdateRequest updateRequestBody, GeneralCallBack<UserDto> generalCallBack) {
        String accessToken = TokenUtils.getAccessToken(context);

        if (TextUtils.isEmpty(accessToken)) {
            generalCallBack.onSuccess(null);
            return;
        }

        userAPI.updateUserInfo(userId, updateRequestBody, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object rawData = response.body().getData();

                    // Đảm bảo ép kiểu đúng
                    Gson gson = new Gson();
                    String json = gson.toJson(rawData);
                    UserDto userDto = gson.fromJson(json, UserDto.class);

                    generalCallBack.onSuccess(userDto);

                    DialogUtils.ShowDialog(context, R.layout.success_dialog, "Thành công", "Cập nhật thông tin thành công");
                } else if (response.code() == 401) {
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            updateUserDetails(userId, updateRequestBody, generalCallBack);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            generalCallBack.onError(new Exception(errorMessage));
                        }
                    });
                } else {
                    try {
                        String errorMessage = response.errorBody().string();
                        generalCallBack.onError(new Exception(errorMessage));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                generalCallBack.onError(throwable);
            }
        });
    }

    public void changePassword(Long userId, UserPasswordUpdateRequest userPasswordUpdateRequest, GeneralCallBack<String> generalCallBack) {
        String accessToken = TokenUtils.getAccessToken(context);

        if (TextUtils.isEmpty(accessToken)) {
            generalCallBack.onSuccess(null);
            return;
        }

        userAPI.updateUserPassword(userId, userPasswordUpdateRequest, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    generalCallBack.onSuccess("Password change successful!");
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            changePassword(userId, userPasswordUpdateRequest, generalCallBack);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            generalCallBack.onError(new Exception(errorMessage));
                        }
                    });
                } else {
                    String errorMessage = response.errorBody() != null ? "Mật khẩu cũ không chính xác" : "Unknown error";
                    generalCallBack.onError(new Exception(errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                generalCallBack.onError(throwable);
            }
        });
    }
}
