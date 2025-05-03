package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface CartAPI {
    @GET("carts/cart/id/{cartId}")
    Call<ApiResponse> getCart(
            @Path("cartId") Long cartId,
            @Header("Authorization") String token
    );

    @GET("carts/cart/user-id/{userId}")
    Call<ApiResponse> getCartByUserId(
            @Path("userId") Long userId,
            @Header("Authorization") String token
    );

    @GET("carts/cart/{cartId}/clear")
    Call<ApiResponse> clearCart(@Path("cartId") Long cartId);

    @GET("carts/cart/total-amount/{cartId}")
    Call<ApiResponse> getTotalAmount(@Path("cartId") Long cartId);
}
