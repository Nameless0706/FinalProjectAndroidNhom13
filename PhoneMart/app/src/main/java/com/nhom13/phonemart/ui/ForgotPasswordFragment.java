package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.AuthAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.ui.auth.LoginFragment;
import com.nhom13.phonemart.ui.auth.RegisterFragment;
import com.nhom13.phonemart.util.FragmentUtils;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment extends Fragment implements View.OnClickListener{

    private Button signUpBtn, sendBtn;
    private TextView returnTv;

    private EditText forgotEmailEt;

    private AuthAPI authAPI;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    public static ForgotPasswordFragment newInstance() {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authAPI = RetrofitClient.getClient().create(AuthAPI.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here
        Mapping(view);
        returnTv.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);

    }

    private void Mapping(View view) {
        returnTv = view.findViewById(R.id.forgotPassword_returnTv);
        signUpBtn = view.findViewById(R.id.forgotPassword_signupBtn);
        sendBtn = view.findViewById(R.id.sendOtpBtn);
        forgotEmailEt = view.findViewById(R.id.email_forgotEt);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.forgotPassword_returnTv){
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        else if (view.getId() == R.id.forgotPassword_signupBtn){
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, new RegisterFragment());
        }
        else{
            sendConfirmOtp();
        }

    }

    private void sendConfirmOtp(){
        String email = forgotEmailEt.getText().toString();
        authAPI.forgotPassword(email).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){
                    FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, VerifyOtpFragment.newInstance("forgot_password", email));
                }
                else {
                    try {
                        //Chỉ lấy field message từ json
                        //String errorBody = response.errorBody().string();
                        //JsonObject jsonObject = JsonParser.parseString(errorBody).getAsJsonObject();
                        //String errorMessage = jsonObject.get("message").getAsString();

                        Gson gson = new Gson();
                        ApiResponse apiResponse = gson.fromJson(response.errorBody().string(), ApiResponse.class);

                        //Hiển thị thông báo lỗi
                        Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.d("Error", Objects.requireNonNull(throwable.getMessage()));
            }
        });

    }




}