package com.nhom13.phonemart.service;

import com.nhom13.phonemart.api.PaymentAPI;
import com.nhom13.phonemart.api.ProductAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.response.PaymentResponse;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentService {
    private PaymentAPI paymentAPI;

    public PaymentService() {
        this.paymentAPI = RetrofitClient.getClient().create(PaymentAPI.class);
    }

    public void createPayment(BigDecimal total, GeneralCallBack<String> generalCallBack){
        paymentAPI.createPayment(total).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful()){
                    generalCallBack.onSuccess(response.body().getUrl());
                }
                else{
                    generalCallBack.onError(new Exception("Không thể tải url VNPay"));
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable throwable) {
                generalCallBack.onError(throwable);
            }
        });
    }
}
