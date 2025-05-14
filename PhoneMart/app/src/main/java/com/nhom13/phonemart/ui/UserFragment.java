package com.nhom13.phonemart.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom13.phonemart.MainActivity;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.OrderDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.enums.OrderStatus;
import com.nhom13.phonemart.service.AuthService;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.service.UserService;
import com.nhom13.phonemart.ui.auth.LoginFragment;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.ImageUtils;
import com.nhom13.phonemart.util.TokenUtils;

public class UserFragment extends Fragment implements View.OnClickListener {

    private static final String LOGIN_USER = "login_user";
    private UserService userService;
    private ImageView profileImg, editUserDetailImg, editUserPasswordImg, button_logout;
    private TextView userNameTv, textView_user_orderNumber, textView_user_favoriteProductNumber, textView_user_shippingOrderNumber;
    private LinearLayout linearLayout_orders, linearLayout_favorites, linearLayout_shipping;
    private UserDto loginUser;
    BottomNavigationView navBar;

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

        userService = new UserService(requireContext());
        navBar = requireActivity().findViewById(R.id.bottom_nav_bar);

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
        mappingEvent();
        mappingData();
    }

    private void mappingEvent() {
        editUserDetailImg.setOnClickListener(this);
        editUserPasswordImg.setOnClickListener(this);
        linearLayout_orders.setOnClickListener(this);
        linearLayout_favorites.setOnClickListener(this);
        linearLayout_shipping.setOnClickListener(this);

        button_logout.setOnClickListener(this);
    }

    private void mappingData() {
        userService.getUserDto(loginUser.getId(), new GeneralCallBack<UserDto>() {
            @Override
            public void onSuccess(UserDto result) {
                if (result != null) {
                    ImageDto loginUserImageDto = result.getImage();
                    if (loginUserImageDto != null) {
                        ImageUtils.loadImageIntoImageView(getContext(), (long) result.getImage().getId(), profileImg);
                    }
                    // trường hợp user chưa upload ảnh thì xuất ảnh mặc định
                    else {
                        Glide.with(requireContext())
                                .load(R.drawable.profile)
                                .into(profileImg);
                    }

                    String name = result.getFirstName() + " " + result.getLastName();
                    userNameTv.setText(name);

                    textView_user_orderNumber.setText(String.valueOf(result.getOrders().size()));
                    textView_user_favoriteProductNumber.setText(String.valueOf(result.getFavoriteProducts().size()));

                    int count = 0;
                    for (OrderDto orderDto : result.getOrders()) {
                        if (orderDto.getOrderStatus().equals(OrderStatus.SHIPPED)) {
                            count++;
                        }
                    }
                    textView_user_shippingOrderNumber.setText(String.valueOf(count));
                }
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoginLogoutIcon(ImageView button_logout) {
        String accessToken = TokenUtils.getAccessToken(requireContext());

        if (!TextUtils.isEmpty(accessToken)){
            button_logout.setImageResource(R.drawable.baseline_logout_24);
        } else {
            button_logout.setImageResource(R.drawable.baseline_login_24);
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

        setLoginLogoutIcon(button_logout);
    }

    private void getUpdatedUser() {
        userService.getUserDto(loginUser.getId(), new GeneralCallBack<UserDto>() {
            @Override
            public void onSuccess(UserDto result) {
                if (result != null) {
                    loginUser = result;
                    userNameTv.setText(String.format("%s %s", result.getFirstName(), result.getLastName()));
                }
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.editUserDetailImg) {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, UserDetailFragment.newInstance(loginUser));
        } else if (view.getId() == R.id.linearLayout_orders) {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, OrderFragment.newInstance(loginUser.getId(), "None"));
        } else if (view.getId() == R.id.linearLayout_favorites) {
            // chỉ cần setTab là được vì cơ bản đây là hành động nhấn tab nên userId được truyền từ bên BaseFragment
            navBar.setSelectedItemId(R.id.favorite);
        } else if (view.getId() == R.id.linearLayout_shipping) {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, OrderFragment.newInstance(loginUser.getId(), "Shipping"));
        } else if (view.getId() == R.id.button_logout) {
            logoutLogin();
        } else if (view.getId() == R.id.editUserPasswordImg) {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, UpdatePasswordFragment.newInstance(loginUser));

        }
    }

    private void logoutLogin() {
        String accessToken = TokenUtils.getAccessToken(requireContext());

        if (!TextUtils.isEmpty(accessToken)){
            AuthService authService = new AuthService(requireContext());
            authService.logout(new GeneralCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        navBar.setSelectedItemId(R.id.home);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.VISIBLE);
        getUpdatedUser();

    }
}