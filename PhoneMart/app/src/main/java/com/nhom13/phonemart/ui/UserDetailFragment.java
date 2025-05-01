package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.User;
import com.nhom13.phonemart.util.ImageUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailFragment extends Fragment implements View.OnClickListener{

    private ImageView backImg, profileImg;

    private static final String EDIT_USER = "edit_user";

    private UserDto edit_user;
    // TODO: Rename and change types of parameters


    public UserDetailFragment() {
        // Required empty public constructor
    }

    public static UserDetailFragment newInstance(UserDto edit_user) {
        UserDetailFragment fragment = new UserDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(EDIT_USER, edit_user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            edit_user = getArguments().getParcelable(EDIT_USER);
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

        backImg.setOnClickListener(this);




        ImageDto loginUserImageDto = edit_user.getImage();
        if (loginUserImageDto != null){
            ImageUtils.loadImageIntoImageView(getContext(), (long) edit_user.getImage().getId(), profileImg);
            backImg.setOnClickListener(this);
        }

        // trường hợp user chưa upload ảnh thì xuất ảnh mặc định
        else {
            Glide.with(requireContext())
                    .load(R.drawable.profile)
                    .into(profileImg);
        }
    }

    private void Mapping(View view){
        backImg = (ImageView) view.findViewById(R.id.editUserDetailBackImg);
        profileImg = (ImageView) view.findViewById(R.id.editUserDetailProfileImg);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.editUserDetailBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }
}