package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductAPI {
    @GET("products/all")
    Call<ApiResponse> getAllProducts();

    @GET("products/product/name/{productName}")
    Call<ApiResponse> getProductByName(@Path("productName") String productName);

    @GET("products/sort/by/dateAdded")
    Call<ApiResponse> getSortedProductByDateAdded(@Query("sortOrder") String sortOrder);

    @GET("products/sort/by/price")
    Call<ApiResponse> getSortedProductByPrice(@Query("sortOrder") String sortOrder);

    @GET("products/category/name/{categoryName}")
    Call<ApiResponse> getRelatedProducts(@Path("categoryName") String categoryName);
}
