package com.nhom13.phonemart.ui.auth;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.AuthAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.model.request.CreateUserRequest;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.ui.VerifyOtpFragment;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterFragment extends Fragment implements View.OnClickListener{

    EditText firtNameEt, lastNameEt, emailEt, passwordEt;
    Button registerBtn;
    TextView returnTv;

    AuthAPI authAPI;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here
        Mapping(view);
        registerBtn.setOnClickListener(this);
        returnTv.setOnClickListener(this);

    }


    private void Mapping(View view) {
        registerBtn = view.findViewById(R.id.registerBtn);
        returnTv = view.findViewById(R.id.register_returnTv);
        firtNameEt = view.findViewById(R.id.registerFirstNameEt);
        lastNameEt = view.findViewById(R.id.registerLastNameEt);
        emailEt = view.findViewById(R.id.registerEmailEt);
        passwordEt = view.findViewById(R.id.loginPasswordEt);

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register_returnTv){
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, new LoginFragment());
        }
        else{
            Log.d("Test", "Making api call");
            register();
        }
    }

    private void register(){
        authAPI = RetrofitClient.getClient().create(AuthAPI.class);
        String firstName = firtNameEt.getText().toString();
        String lastName = lastNameEt.getText().toString();
        String email = emailEt.getText().toString();
        String password = passwordEt.getText().toString();


        authAPI.register(new CreateUserRequest(firstName, lastName, email, password)).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()){


                    // Đưa email qua fragment verify
                    FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, VerifyOtpFragment.newInstance(email));
                }
                else{
                    try {

                        String errorBody = response.errorBody().string();

                        // Chỉ lấy field message từ json
                        JsonObject jsonObject = JsonParser.parseString(errorBody).getAsJsonObject();
                        String errorMessage = jsonObject.get("message").getAsString();

                        //Hiển thị thông báo lỗi
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Error reading server response", Toast.LENGTH_LONG).show();
                    } catch (JsonSyntaxException | IllegalStateException e) {
                        Toast.makeText(getContext(), "Unexpected json format", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.d("Error", Objects.requireNonNull(throwable.getMessage()));
            }
        });
    }


}