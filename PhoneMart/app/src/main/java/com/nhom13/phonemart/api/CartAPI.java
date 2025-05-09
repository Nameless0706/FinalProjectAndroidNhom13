package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface CartAPI {
    @GET("carts/cart/user-id/{userId}")
    Call<ApiResponse> getCartByUserId(
            @Path("userId") Long userId,
            @Header("Authorization") String token
    );

}
