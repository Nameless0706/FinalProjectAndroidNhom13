package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.ImageUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener{

    private ImageView backImg, profileImg, editUserDetailImg, editUserPasswordImg;

    private TextView userNameTv;

    private UserDto loginUser;


    public UserFragment() {
        // Required empty public constructor
    }



    public static UserFragment newInstance(UserDto userDto) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putParcelable("login_user", userDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginUser = getArguments().getParcelable("login_user");
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

        backImg.setOnClickListener(this);
        editUserDetailImg.setOnClickListener(this);
        editUserPasswordImg.setOnClickListener(this);

        String name = loginUser.getFirstName() + " " + loginUser.getLastName();
        userNameTv.setText(name);

        ImageDto loginUserImageDto = loginUser.getImage();
        if (loginUserImageDto != null){
            ImageUtils.loadImageIntoImageView(getContext(), (long) loginUser.getImage().getId(), profileImg);
            backImg.setOnClickListener(this);
        }
        // trường hợp user chưa upload ảnh thì xuất ảnh mặc định
        else {
            Glide.with(requireContext())
                    .load(R.drawable.profile)
                    .into(profileImg);
        }
    }

    private void Mapping(View view) {
        backImg = (ImageView) view.findViewById(R.id.userProfileBackImg);
        profileImg = (ImageView) view.findViewById(R.id.userProfileImg);
        editUserDetailImg = (ImageView) view.findViewById(R.id.editUserDetailImg);
        editUserPasswordImg = (ImageView) view.findViewById(R.id.editUserPasswordImg);
        userNameTv = (TextView) view.findViewById(R.id.profileUsernameTv);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.userProfileBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();
        }

        else if (view.getId() == R.id.editUserDetailImg){
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, new UserDetailFragment());
        }

        else{

        }
    }
}