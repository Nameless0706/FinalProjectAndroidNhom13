package com.nhom13.phonemart.service;

import android.content.Context;

import com.google.gson.Gson;
import com.nhom13.phonemart.api.OrderAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.OrderDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.util.TokenUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderService {
    private OrderAPI orderAPI;
    private Context context;

    public OrderService(Context context) {
        this.context = context;

        orderAPI = RetrofitClient.getClient().create(OrderAPI.class);
    }

    public void placeOrder(Long userId, Long branchId, String address, String paymentMethod, String cardType, GeneralCallBack<OrderDto> generalCallBack) {
        String accessToken = TokenUtils.getAccessToken(context);

        orderAPI.placeOrder(userId, branchId, address, paymentMethod, cardType, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object rawData = response.body().getData();

                    Gson gson = new Gson();
                    String json = gson.toJson(rawData);
                    OrderDto orderDto = gson.fromJson(json, OrderDto.class);

                    generalCallBack.onSuccess(orderDto);
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            placeOrder(userId, branchId, address, paymentMethod, cardType, generalCallBack);
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

    public void getOrderById(Long orderId, GeneralCallBack<OrderDto> generalCallBack) {
        String accessToken = TokenUtils.getAccessToken(context);

        orderAPI.getOrderById(orderId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object rawData = response.body().getData();

                    Gson gson = new Gson();
                    String json = gson.toJson(rawData);
                    OrderDto orderDto = gson.fromJson(json, OrderDto.class);

                    generalCallBack.onSuccess(orderDto);
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            getOrderById(orderId, generalCallBack);
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

    public void cancelOrder(Long orderId, GeneralCallBack<OrderDto> generalCallBack){
        String accessToken = TokenUtils.getAccessToken(context);

        orderAPI.cancelOrder(orderId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Object rawData = response.body().getData();

                    Gson gson = new Gson();
                    String json = gson.toJson(rawData);
                    OrderDto orderDto = gson.fromJson(json, OrderDto.class);

                    generalCallBack.onSuccess(orderDto);
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(context);

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(context, jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            cancelOrder(orderId, generalCallBack);
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
