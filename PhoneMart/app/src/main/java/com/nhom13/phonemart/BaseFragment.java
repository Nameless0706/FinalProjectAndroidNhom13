package com.nhom13.phonemart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom13.phonemart.ui.HomePageFragment;
import com.nhom13.phonemart.ui.UserFragment;
import com.nhom13.phonemart.util.FragmentUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseFragment extends Fragment {

    BottomNavigationView navigationView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FIRST_NAME = "Thang";
    private static final String LAST_NAME = "Huynh";

    // TODO: Rename and change types of parameters
    private String firstName, lastName;

    public BaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseFragment newInstance(String firstName, String lastName) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putString(FIRST_NAME, firstName);
        args.putString(LAST_NAME, lastName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            firstName = getArguments().getString(firstName);
            lastName = getArguments().getString(lastName);
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

        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, new HomePageFragment());

        navigationView = view.findViewById(R.id.bottom_nav_bar);

        navigationView.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int idItem = item.getItemId();
            if (idItem == R.id.home)
                selected = new HomePageFragment();
            else if (idItem == R.id.map) {
                selected = new HomePageFragment();
            }
            else if (idItem == R.id.favorite) {
                selected = new HomePageFragment();
            }
            else {
                selected = new UserFragment();
            }
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, selected);
            return true;
        });

    }



}