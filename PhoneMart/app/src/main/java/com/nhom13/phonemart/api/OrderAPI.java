package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderAPI {
    @POST("orders/add")
    Call<ApiResponse> placeOrder(
            @Query("userId") Long userId,
            @Query("branchId") Long branchId,
            @Query("address") String address,
            @Query("paymentMethod") String paymentMethod,
            @Query("cardType") String cardType,
            @Header("Authorization") String token);

    @PUT("orders/cancel")
    Call<ApiResponse> cancelOrder(
            @Query("orderId") Long orderId,
            @Header("Authorization") String token);

    @GET("orders/order/id/{orderId}")
    Call<ApiResponse> getOrderById(
            @Path("orderId") Long orderId,
            @Header("Authorization") String token);

}
