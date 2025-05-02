package com.nhom13.phonemart.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.android.material.textfield.TextInputLayout;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.ImageUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailFragment extends Fragment implements View.OnClickListener{

    private ImageView backImg, profileImg;

    private TextInputLayout firstNameTv, lastNameTv, emailTv;

    private ConstraintLayout imgGroup;

    private Button confirmBtn;

    private static final String EDIT_USER = "edit_user";

    private UserDto edit_user;

    Uri loaded_image_uri = null;

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
                    //Log.d("Uri Stringgg", loaded_image_uri.toString());
                    //Toast.makeText(getContext(), "Image loaded" + loaded_image_uri.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        backImg.setOnClickListener(this);
        imgGroup.setOnClickListener(this);

        firstNameTv.setEndIconOnClickListener(v -> setupEditText(firstNameTv));
        lastNameTv.setEndIconOnClickListener(v -> setupEditText(lastNameTv));
        emailTv.setEndIconOnClickListener(v -> setupEditText(emailTv));




    }

    private void Mapping(View view){
        profileImg = (ImageView) view.findViewById(R.id.editUserDetailProfileImg);
        backImg = (ImageView) view.findViewById(R.id.chooseBackImg);
        imgGroup = (ConstraintLayout) view.findViewById(R.id.userDetailImgGroup);
        firstNameTv = (TextInputLayout) view.findViewById(R.id.userDetailFirstNameTv);
        lastNameTv = (TextInputLayout) view.findViewById(R.id.userDetailLastNameTv);
        emailTv = (TextInputLayout) view.findViewById(R.id.userDetailEmailTv);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.chooseBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        else{
            //Toast.makeText(getContext(), "Container clicked", Toast.LENGTH_SHORT).show();
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, new ChooseImageFragment());
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

    @Override
    public void onResume() {
        super.onResume();
        if (loaded_image_uri != null) {
            Glide.with(requireContext())
                    .load(loaded_image_uri)
                    .into(profileImg);
            //Log.d("Uri", "Loaded selected image: " + loaded_image_uri);
        } else {
            ImageUtils.loadImageIntoImageView(getContext(), (long) edit_user.getImage().getId(), profileImg);
            //Log.d("Uri", "Loaded default user image from server");
        }
    }

}