package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.response.ApiResponse;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageAPI {
    @GET("images/image/download/{imageId}")
    Call<ResponseBody> loadImage(@Path("imageId") Long imageId);

    @Multipart
    @PUT("images/image/{imageId}/update")
    Call<ApiResponse> updateImage(@Part MultipartBody.Part multipartFile,
                                  @Path("imageId") Long imageId,
                                  @Header("Authorization") String token);
}
