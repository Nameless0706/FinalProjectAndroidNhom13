package com.nhom13.phonemart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.ui.FavoriteFragment;
import com.nhom13.phonemart.ui.HomePageFragment;
import com.nhom13.phonemart.ui.MapsFragment;
import com.nhom13.phonemart.ui.UserFragment;
import com.nhom13.phonemart.util.FragmentUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseFragment extends Fragment {

    private static final String LOGIN_USER = "login_user";

    BottomNavigationView navigationView;

    private UserDto user;


    public BaseFragment() {
        // Required empty public constructor
    }

    public static BaseFragment newInstance(UserDto user) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putSerializable(LOGIN_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (UserDto) getArguments().getSerializable(LOGIN_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here

        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, HomePageFragment.newInstance(user));

        navigationView = view.findViewById(R.id.bottom_nav_bar);

        navigationView.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int idItem = item.getItemId();
            if (idItem == R.id.home)
                selected = HomePageFragment.newInstance(user);
            else if (idItem == R.id.map) {
                selected = new MapsFragment();
            }
            else if (idItem == R.id.favorite) {
                selected = FavoriteFragment.newInstance(user.getId());
            }
            else {
                selected = UserFragment.newInstance(user);
            }
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, selected);
            return true;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.setSelectedItemId(R.id.home);
    }
}