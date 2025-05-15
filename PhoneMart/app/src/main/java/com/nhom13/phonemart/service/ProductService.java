package com.nhom13.phonemart.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.ProductAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.BranchDto;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.util.DialogUtils;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductService {
    private ProductAPI productAPI;

    public ProductService(){
        productAPI = RetrofitClient.getClient().create(ProductAPI.class);
    }


    public void updateProductSoldCount(Long productId, int quantity, GeneralCallBack<String> callback){
        productAPI.updateProductSoldCount(productId, quantity).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){
                    callback.onSuccess(response.body().getMessage());
                }
                else{
                    callback.onError(new Exception("Không thể cập nhật số luợng đã bán của sản phẩm. Mã lỗi: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                callback.onError(throwable);

            }
        });
    }

    public void getAllProducts(GeneralCallBack<List<ProductDto>> callback){
        productAPI.getAllProducts().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if(response.isSuccessful()){

                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getData());
                        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                        List<ProductDto> productDtos = gson.fromJson(json, listType);

                        callback.onSuccess(productDtos);
                    } catch (Exception e) {
                        Log.e("ParseError", "Failed to parse product data", e);
                        callback.onError(e);
                    }
                }

                else{
                    callback.onError(new Exception("Không thể lấy danh sách sản phẩm"));
                }

            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void getProductsByName(String productName, GeneralCallBack<List<ProductDto>> callback){
        productAPI.getProductByName(productName).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if(response.isSuccessful()){

                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getData());
                        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                        List<ProductDto> productDtos = gson.fromJson(json, listType);

                        callback.onSuccess(productDtos);
                    } catch (Exception e) {
                        Log.e("ParseError", "Failed to parse product data", e);
                        callback.onError(e);
                    }
                }

                else{
                    callback.onError(new Exception("Không thể tìm sản phẩm với tên " + productName));
                }

            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void getProductByCategoryName(String categoryName, GeneralCallBack<List<ProductDto>> callback){
        productAPI.getProductByCategory(categoryName).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                    List<ProductDto> productDtos = gson.fromJson(json, listType);
                    callback.onSuccess(productDtos);
                } else {
                    callback.onError(new Exception("Không thể tải sản phẩmm trong danh mục " + categoryName));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    public void getRelatedProducts(String categoryName, GeneralCallBack<List<ProductDto>> generalCallBack) {
        productAPI.getRelatedProducts(categoryName).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Object data = response.body().getData();
                        Gson gson = new Gson();
                        String json = gson.toJson(data);

                        Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                        List<ProductDto> productDtos = gson.fromJson(json, listType);

                        generalCallBack.onSuccess(productDtos);
                    } catch (Exception e) {
                        generalCallBack.onError(e);
                    }
                } else {
                    generalCallBack.onError(new Exception("Response not successful or body is null"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                generalCallBack.onError(t);
            }
        });
    }
}


