package com.nhom13.phonemart.ui;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.ProductAdapter;
import com.nhom13.phonemart.adapter.RecyclerViewInterface;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.Product;
import com.nhom13.phonemart.util.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllProductFragment extends Fragment implements RecyclerViewInterface, View.OnClickListener, TabLayout.OnTabSelectedListener{

    private ImageView backImg;

    private RecyclerView rvProduct;

    private ProductAdapter adapter;
    private TabLayout tabLayout;

    private List<ProductDto> productList;

    private static final String ARG_PRODUCTS = "PRODUCTS";
    private String currentApiUrl = "api/moi-nhat";




    public AllProductFragment() {
        // Required empty public constructor
    }

    public static AllProductFragment newInstance(List<Product> productList) {
        AllProductFragment fragment = new AllProductFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        getAllProducts();
        tabLayout.addOnTabSelectedListener(this);
        setAdapter();

    }

    public void Mapping(View view){
        tabLayout = view.findViewById(R.id.filterTabLayout);
        rvProduct = view.findViewById(R.id.rvProducts);
        backImg = view.findViewById(R.id.allProductBackImg);
    }

    private void getAllProducts(){
        productList = new ArrayList<>();

    }

    private void setAdapter(){
        adapter = new ProductAdapter(getContext(), productList, this);
        rvProduct.setAdapter(adapter);
        int spanCount = 2; // number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        rvProduct.setLayoutManager(gridLayoutManager);

        backImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.allProductBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();
        }
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

    @Override
    public void onItemClick(int position, String source) {
        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, new ProductDetailFragment());
    }


}