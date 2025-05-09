package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface CartItemAPI {
    @POST("cart-items/add")
    Call<ApiResponse> addCartItem(
            @Query("productId") Long productId,
            @Query("quantity") int quantity,
            @Header("Authorization") String token
    );

    @DELETE("cart-items/delete")
    Call<ApiResponse> deleteCartItem(
            @Query("cartId") Long cartId,
            @Query("productId") Long productId,
            @Header("Authorization") String token);

    @PUT("cart-items/update")
    Call<ApiResponse> updateCartItem
            (@Query("cartId") Long cartId,
             @Query("productId") Long productId,
             @Query("quantity") int quantity,
             @Header("Authorization") String token);
}
