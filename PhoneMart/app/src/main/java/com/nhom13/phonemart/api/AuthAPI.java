package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.Category;
import com.nhom13.phonemart.model.request.CreateUserRequest;
import com.nhom13.phonemart.model.request.LoginRequest;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface AuthAPI {
    @POST("/api/v1/auth/register")
    Call<ApiResponse> register(@Body CreateUserRequest userRequest);

    @POST("/api/v1/auth/verify")
    Call<ApiResponse> verify(@Query("email") String email, @Query("otp") String otp);

    @POST("/api/v1/auth/login")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/v1/auth/forgot")
    Call<ApiResponse> forgotPassword(@Query("email") String email);

    @POST("/api/v1/auth/reset-password")
    Call<ApiResponse> resetPassword(@Query("email") String email, @Query("newPassword") String newPassword, @Query("otp") String otp);







}
