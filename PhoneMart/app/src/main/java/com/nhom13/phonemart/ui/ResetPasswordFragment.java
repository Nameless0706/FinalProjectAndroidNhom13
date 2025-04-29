package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.AuthAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.ui.auth.LoginFragment;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment implements View.OnClickListener{
    private static final String USER_EMAIL = "email";

    private static final String OTP = "";

    private String user_email, otp;

    private EditText newPasswordEt, confirmPasswordEt;
    private Button confirmBtn;

    private AuthAPI authAPI;




    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    public static ResetPasswordFragment newInstance(String email, String otp) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        Bundle args = new Bundle();
        args.putString(USER_EMAIL, email);
        args.putString(OTP, otp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user_email = getArguments().getString(USER_EMAIL);
            otp = getArguments().getString(OTP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here
        Mapping(view);

        confirmBtn.setOnClickListener(this);

    }

    private void Mapping(View view){
        confirmBtn = (Button) view.findViewById(R.id.resetPasswordBtn);
        newPasswordEt = (EditText) view.findViewById(R.id.newPasswordEt);
        confirmPasswordEt = (EditText) view.findViewById(R.id.confirmPasswordEt);
    }

    private void resetPassword(){
        String newPassword = newPasswordEt.getText().toString();
        String confirmPassword = confirmPasswordEt.getText().toString();
        if(TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)){
            DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Thất bại", "Vui lòng điền đầy đủ thông tin");
            return;
        }

        authAPI = RetrofitClient.getClient().create(AuthAPI.class);
        authAPI.resetPassword(user_email, newPassword, otp).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()){
                    DialogUtils.ShowDialog(getContext(), R.layout.success_dialog,"Thành công", "Thay đổi mật khẩu thành công");
                    FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, new LoginFragment());
                }
                else{
                    Log.d("ResetPasswordFragment", "Error: " + response.body().getMessage());
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog,"Thất bại", "Thay đổi mật khẩu không thành công");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.d("ResetPasswordFragment", "Error: " + throwable.getMessage());

            }
        });
    }


    @Override
    public void onClick(View view) {
        resetPassword();
    }
}