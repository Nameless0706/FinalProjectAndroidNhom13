package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.util.FragmentUtils;

import java.math.BigDecimal;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFilterFragment extends Fragment implements View.OnClickListener{

    private EditText minPriceEt, maxPriceEt;

    private ImageView backImg;

    private Button confirmBtn;

    private static final String USER_ID = "user_id";

    private static final String SEARCH_STR = "search_str";


    // TODO: Rename and change types of parameters
    private Long userId;

    private String searchStr;

    public ProductFilterFragment() {
        // Required empty public constructor
    }


    public static ProductFilterFragment newInstance(Long userId, String searchStr) {
        ProductFilterFragment fragment = new ProductFilterFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        args.putString(SEARCH_STR, searchStr);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID);
            searchStr = getArguments().getString(searchStr);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here
        Mapping(view);

        backImg.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

    }

    private void applyFilter() {
        String minPriceStr = minPriceEt.getText().toString().trim();
        String maxPriceStr = maxPriceEt.getText().toString().trim();

        BigDecimal minPrice = minPriceStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(minPriceStr);
        BigDecimal maxPrice = maxPriceStr.isEmpty() ? new BigDecimal("999999999") : new BigDecimal(maxPriceStr);

        Bundle result = new Bundle();
        result.putString("minPrice", minPrice.toString());
        result.putString("maxPrice", maxPrice.toString());

        getParentFragmentManager().setFragmentResult("priceFilter", result);

        //Dòng này còn đang bị lỗi
        //FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, AllProductFragment.newInstance(userId, searchStr));
    }


    private void Mapping(View view){
        backImg = (ImageView) view.findViewById(R.id.filterProductBackImg);
        minPriceEt = (EditText) view.findViewById(R.id.minPriceEt);
        maxPriceEt = (EditText) view.findViewById(R.id.maxPriceEt);
        confirmBtn = (Button) view.findViewById(R.id.productFilterConfirmBtn);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.filterProductBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        else{
            applyFilter();
        }
    }



    @Override
    public void onResume() {
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.GONE);
        super.onResume();

    }
}