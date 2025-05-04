package com.nhom13.phonemart.util;

import android.content.Context;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.AuthAPI;
import com.nhom13.phonemart.api.ImageAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.UserDto;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUtils {
    public static void loadImageIntoImageView(Context context, Long imageId, ImageView imageView) {
        ImageAPI imageAPI = RetrofitClient.getClient().create(ImageAPI.class);

        imageAPI.loadImage(imageId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Glide.with(context)
                                .load(response.body().bytes())
                                .centerCrop()
                                .into(imageView);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    //Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                    Glide.with(context)
                            .load(R.drawable.profile)
                            .into(imageView);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void loadUserImage(Context context,UserDto loginUser, ImageView profileImg){
        ImageDto loginUserImageDto = loginUser.getImage();

        if (loginUserImageDto != null){
            ImageUtils.loadImageIntoImageView(context, (long) loginUser.getImage().getId(), profileImg);
        }
        // trường hợp user chưa upload ảnh thì xuất ảnh mặc định
        else {
            Glide.with(context)
                    .load(R.drawable.profile)
                    .into(profileImg);
        }
    }
    private static void logByteArray(byte[] bytes) {
        if (bytes != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b)); // hex format (easy to read)
            }
            android.util.Log.d("ImageBytes", sb.toString());
        } else {
            android.util.Log.d("ImageBytes", "Byte array is null");
        }
    }



}
