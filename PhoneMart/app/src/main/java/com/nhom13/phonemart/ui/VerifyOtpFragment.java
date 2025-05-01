package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.AuthAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.model.request.CreateUserRequest;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.ui.auth.LoginFragment;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifyOtpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyOtpFragment extends Fragment implements View.OnClickListener{

    private Button confirmBtn;

    private PinView otp;

    private AuthAPI authAPI;

    private TextView resendTv;

    private static final String USER_EMAIL = "email";
    private static final String CONTEXT = "context";


    private String user_email;

    private String context;

    public VerifyOtpFragment() {
        // Required empty public constructor
    }


    public static VerifyOtpFragment newInstance(String context, String email) {
        VerifyOtpFragment fragment = new VerifyOtpFragment();
        Bundle args = new Bundle();
        args.putString(USER_EMAIL, email);
        args.putString(CONTEXT, context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_email = getArguments().getString(USER_EMAIL);
            context = getArguments().getString(CONTEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here

        Mapping(view);
        confirmBtn.setOnClickListener(this);
        resendTv.setOnClickListener(this);

    }

    private void Mapping(View view){
        confirmBtn = view.findViewById(R.id.verify_confirmBtn);
        otp = view.findViewById(R.id.otpPv);
        resendTv = view.findViewById(R.id.resendOtpTv);
        authAPI = RetrofitClient.getClient().create(AuthAPI.class);

    }

    private void verify(){

        String otp_txt = otp.getText().toString();


        authAPI.verify(user_email, otp_txt).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    //Hiển thị dialog thành công
                    DialogUtils.ShowDialog(getContext(), R.layout.success_dialog,"Thành công", "Xác thực thành công");
                    Toast.makeText(getContext(), "OTP verified successfully!", Toast.LENGTH_LONG).show();

                    if (context.equals("register")){
                        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, new LoginFragment());
                    }
                    else {
                        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, ResetPasswordFragment.newInstance(user_email));
                    }

                }

                else {
                    try {
                        String errorBody = response.errorBody().string();
                        JsonObject jsonObject = JsonParser.parseString(errorBody).getAsJsonObject();
                        String errorMessage = jsonObject.get("message").getAsString();
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("Error", errorMessage);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Error reading server response", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.d("VerifyOtpFragment", "Error: " + throwable.getMessage());
                //Toast.makeText(getContext(), "Lỗi kết nối API", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void resendOtp(){
        authAPI.resendOtp(user_email).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){
                    DialogUtils.ShowDialog(getContext(), R.layout.success_dialog, "Thành công", "Đã gửi otp thành công");
                }
                else{
                    DialogUtils.ShowDialog(getContext(), R.layout.success_dialog, "Thất bại", "Gửi otp thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.d("VerifyOtpFragment", "Error: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.verify_confirmBtn){
            verify();
        }
        else if (view.getId() == R.id.resendOtpTv){
            resendOtp();
        }
    }
}