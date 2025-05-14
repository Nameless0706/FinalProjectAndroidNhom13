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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.api.UserAPI;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.request.UserPasswordUpdateRequest;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.service.UserService;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.TokenUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpdatePasswordFragment extends Fragment implements View.OnClickListener {


    private ImageView backImg;

    private TextInputLayout oldPasswordEt, newPasswordEt, confirmPasswordEt;

    private Button confirmBtn;
    private static final String EDIT_USER = "edit_user";
    private UserDto edit_user;

    public UpdatePasswordFragment() {
        // Required empty public constructor
    }

    public static UpdatePasswordFragment newInstance(UserDto edit_user) {
        UpdatePasswordFragment fragment = new UpdatePasswordFragment();
        Bundle args = new Bundle();
        args.putSerializable(EDIT_USER, edit_user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            edit_user = (UserDto) getArguments().getSerializable(EDIT_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here
        Mapping(view);

        backImg.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

    }

    private void Mapping(View view) {
        backImg = (ImageView) view.findViewById(R.id.updatePasswordBackImg);
        oldPasswordEt = (TextInputLayout) view.findViewById(R.id.updatePasswordOldPasswordTv);
        newPasswordEt = (TextInputLayout) view.findViewById(R.id.updatePasswordNewPasswordTv);
        confirmPasswordEt = (TextInputLayout) view.findViewById(R.id.updatePasswordConfirmPasswordTv);
        confirmBtn = (Button) view.findViewById(R.id.updatePasswordConfirmBtn);

    }


    private void updateUserPassword() {
        String oldPassword = oldPasswordEt.getEditText().getText().toString();
        String newPassword = newPasswordEt.getEditText().getText().toString();
        String confirmPassword = confirmPasswordEt.getEditText().getText().toString();


        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Thất bại", "Vui lòng điền đầy đủ thông tin");
            return;
        } else if (!newPassword.equals(confirmPassword)) {
            DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Thất bại", "Mật khẩu không trùng khớp");
            return;
        }

        UserPasswordUpdateRequest userPasswordUpdateRequestBody = new UserPasswordUpdateRequest(oldPassword, newPassword);

        UserService userService = new UserService(requireContext());
        userService.changePassword(edit_user.getId(), userPasswordUpdateRequestBody, new GeneralCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    DialogUtils.ShowDialog(requireContext(), R.layout.success_dialog, "Thành công", "Cập nhật mật khẩu thành công");
                    oldPasswordEt.getEditText().setText("");
                    newPasswordEt.getEditText().setText("");
                    confirmPasswordEt.getEditText().setText("");
                } else {
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Load failure", "Please Login!");
                }
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.updatePasswordBackImg) {
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            updateUserPassword();
        }

    }

    @Override
    public void onResume() {
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.GONE);
        super.onResume();

    }


}