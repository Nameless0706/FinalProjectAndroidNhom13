package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom13.phonemart.BaseFragment;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.service.AuthService;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.ImageUtils;

public class UserFragment extends Fragment implements View.OnClickListener {

    private static final String LOGIN_USER = "login_user";
    private ImageView profileImg, editUserDetailImg, editUserPasswordImg;
    private TextView userNameTv, textView_user_orderNumber, textView_user_favoriteProductNumber, textView_user_shippingOrderNumber;
    private LinearLayout linearLayout_orders, linearLayout_favorites, linearLayout_shipping;
    private Button button_logout;
    private UserDto loginUser;

    public UserFragment() {
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
        linearLayout_orders.setOnClickListener(this);
        linearLayout_favorites.setOnClickListener(this);
        linearLayout_shipping.setOnClickListener(this);

        button_logout.setOnClickListener(this);

        String name = loginUser.getFirstName() + " " + loginUser.getLastName();
        userNameTv.setText(name);


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

        textView_user_orderNumber = view.findViewById(R.id.textView_user_orderNumber);
        textView_user_favoriteProductNumber = view.findViewById(R.id.textView_user_favoriteProductNumber);
        textView_user_shippingOrderNumber = view.findViewById(R.id.textView_user_shippingOrderNumber);

        linearLayout_orders = view.findViewById(R.id.linearLayout_orders);
        linearLayout_favorites = view.findViewById(R.id.linearLayout_favorites);
        linearLayout_shipping = view.findViewById(R.id.linearLayout_shipping);

        button_logout = view.findViewById(R.id.button_logout);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.editUserDetailImg) {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, UserDetailFragment.newInstance(loginUser));
        } else if (view.getId() == R.id.linearLayout_orders) {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, OrderFragment.newInstance(loginUser.getId(), "None"));
        } else if (view.getId() == R.id.linearLayout_favorites) {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, FavoriteFragment.newInstance(loginUser.getId()));

            BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
            navBar.setSelectedItemId(R.id.favorite);
        } else if (view.getId() == R.id.linearLayout_shipping) {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, OrderFragment.newInstance(loginUser.getId(), "Shipping"));
        } else if (view.getId() == R.id.button_logout) {
            AuthService authService = new AuthService(requireContext());
            authService.logout();

            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, BaseFragment.newInstance(loginUser));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.VISIBLE);
    }
}