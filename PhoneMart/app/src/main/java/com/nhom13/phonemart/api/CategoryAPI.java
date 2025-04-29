package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryAPI {
    @GET("categories/all")
    Call<ApiResponse> getAllCategories();
}
