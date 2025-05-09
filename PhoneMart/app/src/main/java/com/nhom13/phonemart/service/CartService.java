package com.nhom13.phonemart.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nhom13.phonemart.api.CartAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.CartDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.util.TokenUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartService {
    private CartAPI cartAPI;
    private Context context;

    public CartService(Context context) {
        this.context = context;

        cartAPI = RetrofitClient.getClient().create(CartAPI.class);
    }

    public void getCartByUserId(Long userId, GeneralCallBack<CartDto> generalCallBack) {
        String accessToken = TokenUtils.getAccessToken(context);

        cartAPI.getCartByUserId(userId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    CartDto cartDto = gson.fromJson(json, CartDto.class);

                    generalCallBack.onSuccess(cartDto);
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            getCartByUserId(userId, generalCallBack);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            generalCallBack.onError(new Exception(errorMessage));
                        }
                    });
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        generalCallBack.onError(new Exception("API Error: " + errorBody));
                    } catch (IOException e) {
                        generalCallBack.onError(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                generalCallBack.onError(throwable);
            }
        });
    }
}
