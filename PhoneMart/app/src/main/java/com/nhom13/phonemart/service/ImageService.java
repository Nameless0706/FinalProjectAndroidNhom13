package com.nhom13.phonemart.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhom13.phonemart.api.ImageAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.enums.OwnerType;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.util.TokenUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageService {
    private ImageAPI imageAPI;
    private Context context;

    public ImageService(Context context) {
        this.context = context;

        imageAPI = RetrofitClient.getClient().create(ImageAPI.class);
    }

    public void addImage(MultipartBody.Part filePart, Long userId, GeneralCallBack<List<ImageDto>> generalCallBack) {
        imageAPI.addImage(Collections.singletonList(filePart), userId, (OwnerType.USER)).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());

                    Type listType = new TypeToken<List<ImageDto>>() {
                    }.getType();
                    List<ImageDto> imageDtos = gson.fromJson(json, listType);

                    generalCallBack.onSuccess(imageDtos);
                } else {
                    try {
                        String errorMessage = response.errorBody().string();
                        generalCallBack.onError(new Exception(errorMessage));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                generalCallBack.onError(t);
            }
        });
    }

    public void updateImage(MultipartBody.Part multipartBodyImage, Long imageId, GeneralCallBack<String> generalCallBack) {
        // Image update
        imageAPI.updateImage(multipartBodyImage, imageId).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    generalCallBack.onSuccess(response.body().getMessage());
                } else {
                    try {
                        String errorMessage = response.errorBody().string();
                        generalCallBack.onError(new Exception(errorMessage));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                generalCallBack.onError(t);
            }
        });
    }
}
