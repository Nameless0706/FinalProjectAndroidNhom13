package com.nhom13.phonemart.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.ImageAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.api.UserAPI;
import com.nhom13.phonemart.dto.CartDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.enums.OwnerType;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.request.UserUpdateRequest;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.ImageUtils;
import com.nhom13.phonemart.util.RealPathUtils;
import com.nhom13.phonemart.util.TokenUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailFragment extends Fragment implements View.OnClickListener{

    private ImageView backImg, profileImg;

    private TextInputLayout firstNameEt, lastNameEt;

    private ConstraintLayout imgGroup;

    private Button confirmBtn;

    private static final String EDIT_USER = "edit_user";

    private UserDto edit_user;

    private ImageAPI imageAPI;

    private UserAPI userAPI;

    private Uri loaded_image_uri = null;

    private UserDto updatedUser;

    private boolean isImageChanged = false;


    private final int UNAUTHORIZE_CODE = 401;

    // TODO: Rename and change types of parameters


    public UserDetailFragment() {
        // Required empty public constructor
    }

    public static UserDetailFragment newInstance(UserDto edit_user) {
        UserDetailFragment fragment = new UserDetailFragment();
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
        return inflater.inflate(R.layout.fragment_user_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        Mapping(view);

        requireActivity().getSupportFragmentManager().setFragmentResultListener("image_result", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Lấy kết quả trả về từ ChooseImageFragment
                String imageUriString = result.getString("selectedImageUri");
                if (imageUriString != null) {
                    loaded_image_uri = Uri.parse(imageUriString);
                    Glide.with(requireContext())
                            .load(loaded_image_uri)
                            .into(profileImg);
                    isImageChanged = true;
                    //Log.d("Uri Stringgg", loaded_image_uri.toString());
                    //Toast.makeText(getContext(), "Image loaded" + loaded_image_uri.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        backImg.setOnClickListener(this);
        imgGroup.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        firstNameEt.setEndIconOnClickListener(v -> setupEditText(firstNameEt));
        lastNameEt.setEndIconOnClickListener(v -> setupEditText(lastNameEt));

        firstNameEt.getEditText().setText(edit_user.getFirstName());
        lastNameEt.getEditText().setText(edit_user.getLastName());





    }

    private void Mapping(View view){
        profileImg = (ImageView) view.findViewById(R.id.editUserDetailProfileImg);
        backImg = (ImageView) view.findViewById(R.id.userDetailBackImg);
        imgGroup = (ConstraintLayout) view.findViewById(R.id.userDetailImgGroup);
        firstNameEt = (TextInputLayout) view.findViewById(R.id.userDetailFirstNameTv);
        lastNameEt = (TextInputLayout) view.findViewById(R.id.userDetailLastNameTv);
        confirmBtn = (Button) view.findViewById(R.id.userDetailConfirmBtn);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.userDetailBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();


        }
        else if (view.getId() == R.id.userDetailImgGroup){
            //Toast.makeText(getContext(), "Container clicked", Toast.LENGTH_SHORT).show();
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, new ChooseImageFragment());
        }

        else{
            updateUserDetails();
        }
    }

    private void setupEditText(TextInputLayout layout){
        if (layout.getEditText() != null) {
            layout.getEditText().setEnabled(true);
            layout.getEditText().requestFocus();
            //Toast.makeText(getContext(), "Edit enabled", Toast.LENGTH_SHORT).show();

            layout.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    v.setEnabled(false);
                }
            });
        }
    }

    private void updateUserDetails(){

        String editUserFirstName = firstNameEt.getEditText().getText().toString();
        String editUserLastName = lastNameEt.getEditText().getText().toString();
        String accessToken = TokenUtils.getAccessToken(requireContext());

        UserUpdateRequest updateRequestBody = new UserUpdateRequest(editUserFirstName, editUserLastName);


        userAPI = RetrofitClient.getClient().create(UserAPI.class);
        userAPI.updateUserInfo(edit_user.getId(), updateRequestBody, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()){
                    Toast.makeText(requireContext(), editUserFirstName + " " + editUserLastName, Toast.LENGTH_SHORT).show();
                    updateImage();
                    DialogUtils.ShowDialog(requireContext(), R.layout.success_dialog, "Thành công", "Cập nhật thông tin thành công");
                }
                else if (response.code() == UNAUTHORIZE_CODE){
                    String refreshToken = TokenUtils.getRefreshToken(requireContext());

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            updateUserDetails();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    try {
                        Log.d("error", "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.d("onFailure", "onFailure: " + throwable.getMessage());
            }
        });




    }

    private void updateImage() {
        // Chỉ gọi khi có thay đổi ảnh
        if (isImageChanged) {


            String strRealPath = RealPathUtils.getRealPath(requireContext(), loaded_image_uri);
            Log.d("UploadImage", strRealPath);
            Log.d("UploadImage", "Not using default image");
            File file = new File(strRealPath);


            String token = "Bearer " + TokenUtils.getAccessToken(requireContext());
            imageAPI = RetrofitClient.getClient().create(ImageAPI.class);
            if (edit_user.getImage() != null){

                RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part multipartBodyImage = MultipartBody.Part.createFormData("multipartFile", file.getName(), requestBodyImage);
                long imageId = edit_user.getImage().getId();

                // Image update
                imageAPI.updateImage(multipartBodyImage, imageId, token).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            loaded_image_uri = null;
                            isImageChanged = false;
                            Toast.makeText(requireContext(), "Image uploaded!", Toast.LENGTH_SHORT).show();
                        }
                        else if (response.code() == UNAUTHORIZE_CODE){
                            String refreshToken = TokenUtils.getRefreshToken(requireContext());

                            TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                                @Override
                                public void onSuccess(JwtResponse jwtResponse) {
                                    // lưu lại token mới
                                    TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                                    // gọi lại API với token mới
                                    updateImage();
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            Log.e("Image Upload", "Failed with code " + response.code());
                            Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                        Log.e("Image Upload", "Error: " + t.getMessage());
                        Toast.makeText(requireContext(), "Image upload error", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            else{

                RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("multipartFiles", file.getName(), requestBodyImage);
                imageAPI.addImage(Collections.singletonList(filePart), edit_user.getId(), (OwnerType.USER)).enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            loaded_image_uri = null;
                            isImageChanged = false;
                            Toast.makeText(requireContext(), "Image added!", Toast.LENGTH_SHORT).show();
                        }
                        else if (response.code() == UNAUTHORIZE_CODE){
                            String refreshToken = TokenUtils.getRefreshToken(requireContext());

                            TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                                @Override
                                public void onSuccess(JwtResponse jwtResponse) {
                                    // lưu lại token mới
                                    TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                                    // gọi lại API với token mới
                                    updateImage();
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            Log.e("Image Adding", "Failed with code " + response.code());
                            try {
                                Log.d("error", "onResponse: " + response.errorBody().string());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Toast.makeText(requireContext(), "Failed to add image", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                        Log.e("Image add", "Error: " + t.getMessage());
                        Toast.makeText(requireContext(), "Image upload error", Toast.LENGTH_SHORT).show();
                    }
                });
            }



        } else {
            Log.d("Image Upload", "Skipped upload because user is using default image.");
        }
    }


    @Override
    public void onResume() {
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.GONE);

        super.onResume();
        if (loaded_image_uri != null) {
            Glide.with(requireContext())
                    .load(loaded_image_uri)
                    .into(profileImg);
            //Log.d("Uri", "Loaded selected image: " + loaded_image_uri);
        } else {
            if (edit_user.getImage() != null){
                ImageUtils.loadImageIntoImageView(getContext(), (long) edit_user.getImage().getId(), profileImg);
            }
            else{
                Log.d("Null image", "Null");
            }
            //Log.d("Uri", "Loaded default user image from server");
        }
    }



}