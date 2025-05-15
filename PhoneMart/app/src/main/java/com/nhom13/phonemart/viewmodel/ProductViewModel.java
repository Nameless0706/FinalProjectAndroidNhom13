package com.nhom13.phonemart.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhom13.phonemart.api.ProductAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.CategoryDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.response.ApiResponse;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {

    private final MutableLiveData<List<ProductDto>> productList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final ProductAPI productAPI;

    public ProductViewModel() {
        productAPI = RetrofitClient.getClient().create(ProductAPI.class);
    }

    public LiveData<List<ProductDto>> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductDto> products) {
        productList.setValue(products);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }

    public boolean hasData() {
        return productList.getValue() != null && !productList.getValue().isEmpty();
    }

    public void clearProducts() {
        if (productList.getValue() != null) {
            productList.getValue().clear();
            productList.setValue(productList.getValue());
        }
    }

    // API Calls
    public void getAllProducts() {
        setLoading(true);
        productAPI.getAllProducts().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                    List<ProductDto> products = gson.fromJson(json, listType);
                    setProductList(products);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                setLoading(false);
            }
        });
    }

    public void getProductsByName(String productName) {
        setLoading(true);
        productAPI.getProductByName(productName).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                    List<ProductDto> products = gson.fromJson(json, listType);
                    setProductList(products);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                setLoading(false);
            }
        });
    }

    public void getProductsByCategory(String categoryName) {
        setLoading(true);
        productAPI.getProductByCategory(categoryName).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                    List<ProductDto> products = gson.fromJson(json, listType);
                    setProductList(products);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                setLoading(false);
            }
        });
    }
}

