package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.request.CreateUserRequest;
import com.nhom13.phonemart.model.request.GoogleLoginRequest;
import com.nhom13.phonemart.model.request.LoginRequest;
import com.nhom13.phonemart.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthAPI {
    @POST("auth/register")
    Call<ApiResponse> register(@Body CreateUserRequest userRequest);

    @POST("auth/verify")
    Call<ApiResponse> verify(@Query("email") String email, @Query("otp") String otp);

    @POST("auth/login")
    Call<ApiResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/forgot")
    Call<ApiResponse> forgotPassword(@Query("email") String email);

    @POST("auth/reset-password")
    Call<ApiResponse> resetPassword(@Query("email") String email, @Query("newPassword") String newPassword);

    @POST("auth/resend-otp")
    Call<ApiResponse> resendOtp(@Query("email") String email);

    @POST("/api/v1/auth/login/google")
    Call<ApiResponse> loginWithGoogle(@Body GoogleLoginRequest request);

    @POST("auth/refresh-token")
    Call<ApiResponse> createNewAccessToken(@Query("reft") String refreshToken);

    @POST("auth/logout")
    Call<ApiResponse> logout(@Query("refreshToken") String refreshToken);
}
