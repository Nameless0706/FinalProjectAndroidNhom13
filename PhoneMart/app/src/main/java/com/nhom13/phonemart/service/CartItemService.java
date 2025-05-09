package com.nhom13.phonemart.service;

import android.content.Context;

import com.nhom13.phonemart.api.CartItemAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.util.TokenUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartItemService {
    private CartItemAPI cartItemAPI;
    private Context context;

    public CartItemService(Context context){
        this.context = context;

        cartItemAPI = RetrofitClient.getClient().create(CartItemAPI.class);
    }

    public void updateCartItem(Long cartId, Long productId, int quantity, GeneralCallBack<String> generalCallBack) {
        String accessToken = TokenUtils.getAccessToken(context);

        cartItemAPI.updateCartItem(cartId, productId, quantity, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            updateCartItem(cartId, productId, quantity, generalCallBack);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            generalCallBack.onError(new Exception(errorMessage));
                        }
                    });
                }

                generalCallBack.onSuccess(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                generalCallBack.onError(throwable);
            }
        });
    }

    public void deleteCartItem(Long cartId, Long productId, GeneralCallBack<String> generalCallBack) {
        String accessToken = TokenUtils.getAccessToken(context);

        cartItemAPI.deleteCartItem(cartId, productId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            deleteCartItem(cartId, productId, generalCallBack);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            generalCallBack.onError(new Exception(errorMessage));
                        }
                    });
                }

                generalCallBack.onSuccess(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                generalCallBack.onError(throwable);
            }
        });
    }

    public void addProductToCart(int viewId, Long productId, GeneralCallBack<String> generalCallBack) {
        String accessToken = TokenUtils.getAccessToken(context);

        cartItemAPI.addCartItem(productId, 1, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    generalCallBack.onSuccess(response.body().getMessage());
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            addProductToCart(viewId, productId, generalCallBack);
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
}
