package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProductAPI {
    @GET("products/all")
    Call<ApiResponse> getAllProducts();
}
