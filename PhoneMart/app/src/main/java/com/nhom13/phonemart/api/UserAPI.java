package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.request.UserUpdateRequest;
import com.nhom13.phonemart.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserAPI {

    @GET("users/user/id/{userId}")
    Call<ApiResponse> getUserById (@Path("userId") Long userId,
                               @Header("Authorization") String token);
    @PUT("users/user/{userId}/update")
    Call<ApiResponse> updateUserInfo(
            @Path("userId") Long userId,
            @Body UserUpdateRequest userUpdateRequest,
            @Header("Authorization") String token);
    @PUT("users/user/favorite/{userId}/{productId}")
    Call<ApiResponse> saveFavoriteProduct(
            @Path("userId") Long userId,
            @Path("productId") Long productId,
            @Header("Authorization") String token
    );
}
