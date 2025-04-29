package com.nhom13.phonemart.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.nhom13.phonemart.BaseFragment;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.AuthAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.request.GoogleLoginRequest;
import com.nhom13.phonemart.model.request.LoginRequest;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.ui.ForgotPasswordFragment;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    private Button loginBtn, button_loginGoogle;
    private TextView signupTv;
    private TextView forgotTv;

    private EditText emailEt, passwordEt;

    private AuthAPI authAPI;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 1000;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        configLoginGoogle();
    }

    private void configLoginGoogle() {
        // Cấu hình yêu cầu đăng nhập
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // Lấy từ Google Console của client-id web
                .requestIdToken("116662858981-qkoic91p9v2stqbi9pd8608e0hqkrcrf.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Mapping
        Mapping(view);

        // Setting click listeners
        loginBtn.setOnClickListener(this);
        button_loginGoogle.setOnClickListener(this);
        signupTv.setOnClickListener(this);
        forgotTv.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here

    }

    private void Mapping(View view) {
        emailEt = view.findViewById(R.id.loginEmailEt);
        passwordEt = view.findViewById(R.id.loginPasswordEt);
        loginBtn = view.findViewById(R.id.loginBtn);
        button_loginGoogle = view.findViewById(R.id.button_loginGoogle);
        signupTv = view.findViewById(R.id.signUpTv);
        forgotTv = view.findViewById(R.id.forgotPasswordTv);
    }


    @Override
    public void onClick(View view) {
        Fragment selected = null;
        if (view.getId() == R.id.loginBtn) {
            login();
            return;
        } else if (view.getId() == R.id.button_loginGoogle) {
            loginWithGoogle();
            return;
        } else if (view.getId() == R.id.signUpTv) {
            selected = new RegisterFragment();
        } else {
            selected = new ForgotPasswordFragment();
        }
        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, selected);

    }

    private void loginWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Lấy ID Token gửi lên server để xác thực
                String idToken = account.getIdToken();
                String email = account.getEmail();
                String name = account.getDisplayName();

                // TODO: Gửi idToken lên server để xác thực người dùng
                sendTokenToBackend(idToken);
            } catch (ApiException e) {
                Log.w("Google Sign-In", "signInResult:failed code=" + e.getStatusCode());
            }
        }
    }

    private void sendTokenToBackend(String idToken) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);

        GoogleLoginRequest googleLoginRequest = new GoogleLoginRequest(idToken);

        authAPI = RetrofitClient.getClient().create(AuthAPI.class);

        authAPI.loginWithGoogle(googleLoginRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Gson gson = new Gson();

                    String json = gson.toJson(response.body().getData());
                    JwtResponse jwt = gson.fromJson(json, JwtResponse.class);

                    rdirectAfterLoginSuccess(jwt);
                } else {
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Thất bại", "Đăng nhập thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("ApiError", "Error: " + t.getMessage());
            }
        });
    }

    private void login() {

        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();

        authAPI = RetrofitClient.getClient().create(AuthAPI.class);

        authAPI.login(new LoginRequest(email, password)).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {

                    Gson gson = new Gson();

                    String json = gson.toJson(response.body().getData());
                    JwtResponse jwt = gson.fromJson(json, JwtResponse.class);


                    rdirectAfterLoginSuccess(jwt);
                } else {
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Thất bại", "Sai email hoặc mật khẩu");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, Throwable throwable) {
                Log.e("ApiError", throwable.getMessage());
            }
        });
    }

    private void rdirectAfterLoginSuccess(JwtResponse jwt) {
        UserDto loginUser = jwt.getUser();
        String accessToken = jwt.getAccessToken();
        String refreshToken = jwt.getRefreshToken();


        SharedPreferences prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("access_token", accessToken)
                .putString("refresh_token", refreshToken)
                .apply();


        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, BaseFragment.newInstance(loginUser));
    }

}