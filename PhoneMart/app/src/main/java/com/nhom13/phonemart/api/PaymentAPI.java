package com.nhom13.phonemart.api;

import com.nhom13.phonemart.model.response.PaymentResponse;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PaymentAPI {
    @GET("payment/create-payment")
    Call<PaymentResponse> createPayment(@Query("total") BigDecimal total);

}
