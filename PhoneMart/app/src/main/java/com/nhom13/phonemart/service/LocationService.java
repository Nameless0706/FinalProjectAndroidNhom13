package com.nhom13.phonemart.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.nhom13.phonemart.model.interfaces.LocationCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService {

    private Context context;

    public LocationService(Context context){
        this.context = context;
    }

    public void getYourCurrentLocation(LocationCallback callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onError("Chưa được cấp quyền truy cập vị trí");
            return;
        }

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        callback.onLocationReceived(latitude, longitude);
                    } else {
                        callback.onError("Không thể lấy vị trí hiện tại");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Lỗi: " + e.getMessage());
                });
    }

    public String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Address", "Lỗi khi chuyển tọa độ thành địa chỉ");
        }

        return null;
    }
}
