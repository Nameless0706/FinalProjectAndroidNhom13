package com.nhom13.phonemart.service;

import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhom13.phonemart.api.BranchAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.BranchDto;
import com.nhom13.phonemart.model.interfaces.BranchCallback;
import com.nhom13.phonemart.model.response.ApiResponse;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchService {

    private BranchAPI branchAPI;

    public BranchService() {
        branchAPI = RetrofitClient.getClient().create(BranchAPI.class);
    }

    public void getAllBranches(BranchCallback callback) {
        branchAPI.getAllBranches().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Object data = response.body().getData();
                        Gson gson = new Gson();
                        String json = gson.toJson(data);

                        Type listType = new TypeToken<List<BranchDto>>() {}.getType();
                        List<BranchDto> branches = gson.fromJson(json, listType);

                        callback.onSuccess(branches);
                    } catch (Exception e) {
                        Log.e("ParseError", "Failed to parse branch data", e);
                        callback.onError(e);
                    }
                } else {
                    callback.onError(new Exception("Response not successful or body is null"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("ApiError", "API call failed: " + t.getMessage());
                callback.onError(t);
            }
        });
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        // Đơn vị: mét
        return results[0];
    }

    public BranchDto findNearestBranch(double currentLat, double currentLng, List<BranchDto> branches) {
        BranchDto nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (BranchDto branchDto : branches) {
            double distance = calculateDistance(currentLat, currentLng, branchDto.getLatitude(), branchDto.getLongitude());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = branchDto;
            }
        }

        return nearest;
    }

}
