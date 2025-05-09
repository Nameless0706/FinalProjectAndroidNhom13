package com.nhom13.phonemart.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.request.UserUpdateRequest;
import com.nhom13.phonemart.service.ImageService;
import com.nhom13.phonemart.service.UserService;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.RealPathUtils;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserDetailFragment extends Fragment implements View.OnClickListener {
    private UserService userService;
    private ImageService imageService;
    private ImageView backImg, profileImg;
    private TextInputLayout firstNameEt, lastNameEt;
    private ConstraintLayout imgGroup;
    private Button confirmBtn;
    private static final String EDIT_USER = "edit_user";
    private UserDto edit_user;
    private Uri loaded_image_uri = null;

    public UserDetailFragment() {
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

        userService = new UserService(requireContext());
        imageService = new ImageService(requireContext());

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

        requireActivity().getSupportFragmentManager().setFragmentResultListener("image_result", getViewLifecycleOwner(), (requestKey, result) -> {
            // Lấy kết quả trả về từ ChooseImageFragment
            String imageUriString = result.getString("selectedImageUri");
            if (imageUriString != null) {
                loaded_image_uri = Uri.parse(imageUriString);
                Glide.with(requireContext())
                        .load(loaded_image_uri)
                        .into(profileImg);
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

    private void Mapping(View view) {
        profileImg = (ImageView) view.findViewById(R.id.editUserDetailProfileImg);
        backImg = (ImageView) view.findViewById(R.id.userDetailBackImg);
        imgGroup = (ConstraintLayout) view.findViewById(R.id.userDetailImgGroup);
        firstNameEt = (TextInputLayout) view.findViewById(R.id.userDetailFirstNameTv);
        lastNameEt = (TextInputLayout) view.findViewById(R.id.userDetailLastNameTv);
        confirmBtn = (Button) view.findViewById(R.id.userDetailConfirmBtn);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.userDetailBackImg) {
            requireActivity().getSupportFragmentManager().popBackStack();
        } else if (view.getId() == R.id.userDetailImgGroup) {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, new ChooseImageFragment());
        } else {
            String editUserFirstName = firstNameEt.getEditText().getText().toString();
            String editUserLastName = lastNameEt.getEditText().getText().toString();

            UserUpdateRequest updateRequestBody = new UserUpdateRequest(editUserFirstName, editUserLastName);

            userService.updateUserDetails(edit_user.getId(), updateRequestBody, new GeneralCallBack<UserDto>() {
                @Override
                public void onSuccess(UserDto result) {
                    updateImage();
                    // quay về fragment trước
                    requireActivity().getSupportFragmentManager().popBackStack();
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupEditText(TextInputLayout layout) {
        if (layout.getEditText() != null) {
            layout.getEditText().setEnabled(true);
            layout.getEditText().requestFocus();

            layout.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    v.setEnabled(false);
                }
            });
        }
    }

    private void updateImage() {
        if (loaded_image_uri == null) {
            return;
        }

        String strRealPath = RealPathUtils.getRealPath(requireContext(), loaded_image_uri);
        File file = new File(strRealPath);

        // update image
        if (edit_user.getImage() != null) {
            RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part multipartBodyImage = MultipartBody.Part.createFormData("multipartFile", file.getName(), requestBodyImage);
            long imageId = edit_user.getImage().getId();

            imageService.updateImage(multipartBodyImage, edit_user.getImage().getId(), new GeneralCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // ko cần xử lý kết quả trả về
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("multipartFiles", file.getName(), requestBodyImage);

            imageService.addImage(filePart, edit_user.getId(), new GeneralCallBack<List<ImageDto>>() {
                @Override
                public void onSuccess(List<ImageDto> result) {
                    // ko cần xử lý kết quả trả về
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.GONE);

//        if (loaded_image_uri != null) {
//            Glide.with(requireContext())
//                    .load(loaded_image_uri)
//                    .into(profileImg);
//        } else {
//            if (edit_user.getImage() != null) {
//                ImageUtils.loadImageIntoImageView(getContext(), (long) edit_user.getImage().getId(), profileImg);
//            } else {
//                Log.d("Null image", "Null");
//            }
//        }
    }

}