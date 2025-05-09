package com.nhom13.phonemart.ui;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.service.UserService;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.ImageUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener{

    private static final String LOGIN_USER = "login_user";

    private ImageView profileImg, editUserDetailImg, editUserPasswordImg;

    private TextView userNameTv;

    private UserDto loginUser;


    public UserFragment() {
        // Required empty public constructor
    }



    public static UserFragment newInstance(UserDto userDto) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putSerializable(LOGIN_USER, userDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginUser = (UserDto) getArguments().getSerializable(LOGIN_USER);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Mapping(view);

        editUserDetailImg.setOnClickListener(this);
        editUserPasswordImg.setOnClickListener(this);


        String name = loginUser.getFirstName() + " " + loginUser.getLastName();
        userNameTv.setText(name);

        getUpdatedUser();



        ImageDto loginUserImageDto = loginUser.getImage();
        if (loginUserImageDto != null) {
            ImageUtils.loadImageIntoImageView(getContext(), (long) loginUser.getImage().getId(), profileImg);
        }
        // trường hợp user chưa upload ảnh thì xuất ảnh mặc định
        else {
            Glide.with(requireContext())
                    .load(R.drawable.profile)
                    .into(profileImg);
        }


    }

    private void Mapping(View view) {
        profileImg = (ImageView) view.findViewById(R.id.userProfileImg);
        editUserDetailImg = (ImageView) view.findViewById(R.id.editUserDetailImg);
        editUserPasswordImg = (ImageView) view.findViewById(R.id.editUserPasswordImg);
        userNameTv = (TextView) view.findViewById(R.id.profileUsernameTv);

    }

    private void getUpdatedUser() {
        UserService userService = new UserService(requireContext());
        userService.getUserDto(loginUser.getId(), new GeneralCallBack<UserDto>() {
            @Override
            public void onSuccess(UserDto result) {
                loginUser = result;
                userNameTv.setText(String.format("%s %s", result.getFirstName(), result.getLastName()));
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.editUserDetailImg){
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, UserDetailFragment.newInstance(loginUser));
        }

        else{
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, UpdatePasswordFragment.newInstance(loginUser));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.VISIBLE);
    }
}