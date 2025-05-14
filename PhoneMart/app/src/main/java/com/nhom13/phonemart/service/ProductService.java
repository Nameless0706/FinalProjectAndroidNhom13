package com.nhom13.phonemart.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhom13.phonemart.api.ProductAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.response.ApiResponse;

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
