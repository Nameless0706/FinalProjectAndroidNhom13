package com.nhom13.phonemart.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImageAPI {
    @GET("images/image/download/{imageId}")
    Call<ResponseBody> loadImage(@Path("imageId") Long imageId);
}
