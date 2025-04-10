package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllProductFragment extends Fragment implements View.OnClickListener, TabLayout.OnTabSelectedListener{

    ImageView backImg;

    RecyclerView rvProduct;
    TabLayout tabLayout;

    List<Product> productList;

    private static final String ARG_PRODUCTS = "PRODUCTS";
    private String currentApiUrl = "api/moi-nhat";




    public AllProductFragment() {
        // Required empty public constructor
    }

    public static AllProductFragment newInstance(List<Product> productList) {
        AllProductFragment fragment = new AllProductFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("product_list", new ArrayList<>(productList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productList = getArguments().getParcelableArrayList(ARG_PRODUCTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Mapping(view);

        tabLayout.addOnTabSelectedListener(this);

    }

    public void Mapping(View view){
        tabLayout = view.findViewById(R.id.filterTabLayout);
        //rvProduct = view.findViewById(R.id.allProdRv);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                Toast.makeText(getContext(), "Hey", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(getContext(), "Hey2", Toast.LENGTH_SHORT).show();

                break;
            case 2:
                Toast.makeText(getContext(), "Hey3", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}