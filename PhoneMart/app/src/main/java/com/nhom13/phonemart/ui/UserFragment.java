package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.util.ImageUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener{

    private ImageView backImg, profileImg;

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

        String name = loginUser.getFirstName() + " " + loginUser.getLastName();
        userNameTv.setText(name);

        ImageUtils.loadImageIntoImageView(getContext(), (long) 2, profileImg);
        backImg.setOnClickListener(this);

    }

    private void Mapping(View view) {
        backImg = (ImageView) view.findViewById(R.id.userProfileBackImg);
        profileImg = (ImageView) view.findViewById(R.id.userProfileImg);
        userNameTv = (TextView) view.findViewById(R.id.profileUsernameTv);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.userProfileBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();
        }

        else{

        }
    }
}