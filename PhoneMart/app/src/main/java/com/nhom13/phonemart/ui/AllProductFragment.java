package com.nhom13.phonemart.ui;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.ProductAdapter;
import com.nhom13.phonemart.adapter.RecyclerViewInterface;
import com.nhom13.phonemart.api.ProductAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.CategoryDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.OnProductItemActionListener;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AllProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AllProductFragment extends Fragment implements View.OnClickListener, TabLayout.OnTabSelectedListener, OnProductItemActionListener, TextView.OnEditorActionListener {

    private EditText searchBar;
    private ImageView backImg, filterImg;

    private RecyclerView rvProduct;

    private ProductAdapter adapter;
    private TabLayout tabLayout;


    private List<ProductDto> productList = new ArrayList<>();


    private ProductAPI productApi;

    private Long userId;

    private String searchString;

    private static final String USER_ID = "user_id";

    private static final String SEARCH_STRING = "search_string";

    private boolean isPriceAscending = true;

    private boolean isDateDescending = true;





    public AllProductFragment() {
        // Required empty public constructor
    }


    public static AllProductFragment newInstance(Long userId, String searchString) {
        AllProductFragment fragment = new AllProductFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        args.putString(SEARCH_STRING, searchString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID);
            searchString = getArguments().getString(SEARCH_STRING);
        }
        productApi = RetrofitClient.getClient().create(ProductAPI.class);

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

        if (searchString == null) {
            getAllProducts();
        }

        else{
            getProductsByName();
        }

        getParentFragmentManager().setFragmentResultListener("priceFilter", this, (requestKey, bundle) -> {

            List<ProductDto> filteredList = new ArrayList<>();
            String minPriceStr = bundle.getString("minPrice");
            String maxPriceStr = bundle.getString("maxPrice");

            BigDecimal minPrice = minPriceStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(minPriceStr);
            BigDecimal maxPrice = maxPriceStr.isEmpty() ? new BigDecimal("999999999") : new BigDecimal(maxPriceStr);

            Log.d("FilteredProductFragment", "Min Price: " + minPrice + ", Max Price: " + maxPrice);

            // TODO: Use these values to fetch data or apply filters
            for (ProductDto product : productList) {
                BigDecimal price = product.getPrice();
                if (price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0) {
                    filteredList.add(product);
                }
            }
            adapter.notifyDataSetChanged();
        });


        tabLayout.addOnTabSelectedListener(this);

        searchBar.setOnEditorActionListener(this);
        backImg.setOnClickListener(this);
        filterImg.setOnClickListener(this);
        searchBar.setText(searchString);




    }

    public void Mapping(View view){
        tabLayout = view.findViewById(R.id.tabLayout);
        rvProduct = view.findViewById(R.id.recycleView_product);
        backImg = view.findViewById(R.id.allProductBackImg);
        filterImg = view.findViewById(R.id.allProductFilterImg);
        searchBar = view.findViewById(R.id.allProductSearchEt);
    }


    private void setAdapter(){
        adapter = new ProductAdapter(getContext(), productList, AllProductFragment.this);
        rvProduct.setAdapter(adapter);
        int spanCount = 2; // number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        rvProduct.setLayoutManager(gridLayoutManager);
    }


    private void getAllProducts(){
        productApi.getAllProducts().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                    productList = gson.fromJson(json, listType);

                    sortProductsByDate(isDateDescending);
                    setAdapter();

                }

                else{
                    productList = null;
                    DialogUtils.ShowDialog(requireContext(), R.layout.error_dialog, "Thất bại", "Không thể tải sản phẩm");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.d("All Product", "onFailure: " + throwable.getMessage());

            }
        });

    }

    private void getProductsByName(){
        productList.clear();
        productApi.getProductByName(searchString).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {

                if (response.isSuccessful()){
                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                    productList = gson.fromJson(json, listType);
                    sortProductsByDate(isDateDescending);
                    setAdapter();

                }

                else{
                    productList = null;
                    DialogUtils.ShowDialog(requireContext(), R.layout.error_dialog, "Th?t b?i", "Kh�ng t�m th?y s?n ph?m v?i t�n " + searchString);
                }


            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.d("All Product", "onFailure: " + throwable.getMessage());

            }
        });



    }

    private void sortProductsByDate(boolean descending) {
        if (productList == null || productList.isEmpty()) {
            Log.d("Sort", "Product list is empty or null");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            productList.sort((p1, p2) -> {
                try {
                    LocalDateTime date1 = LocalDateTime.parse(p1.getDateAdded());
                    LocalDateTime date2 = LocalDateTime.parse(p2.getDateAdded());
                    if(descending){
                        return date2.compareTo(date1);
                    }

                    else{
                        return date1.compareTo(date2);
                    }

                } catch (Exception e) {
                    Log.e("Sort Error", "Could not parse date: " + e.getMessage());
                    return 0;
                }
            });
        }

        //Set l?i flipper
        isDateDescending = !isDateDescending;


        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void sortProductsByPrice(boolean ascending) {
        if (productList == null || productList.isEmpty()) {
            Log.d("Sort", "Product list is empty or null");
            return;
        }
        productList.sort((p1, p2) -> {
            try {
                BigDecimal price1 = p1.getPrice();
                BigDecimal price2 = p2.getPrice();


                if (ascending) {
                    return price1.compareTo(price2); // Ascending order (lowest to highest)
                } else {
                    return price2.compareTo(price1); // Descending order (highest to lowest)
                }
            } catch (Exception e) {
                Log.e("Sort Error", "Could not compare prices: " + e.getMessage());
                return 0;
            }
        });

        //Set l?i flipper
        isPriceAscending = !isPriceAscending;

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }




    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.allProductBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        else{
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, new ProductFilterFragment());
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                Toast.makeText(getContext(), "Hey", Toast.LENGTH_SHORT).show();
                sortProductsByDate(isDateDescending);
                break;
            case 1:
                Toast.makeText(getContext(), "Hey2", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getContext(), "Hey3", Toast.LENGTH_SHORT).show();
                sortProductsByPrice(isPriceAscending);
                break;

        }
    }


    @Override

    public void onTabReselected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                Toast.makeText(getContext(), "Hey", Toast.LENGTH_SHORT).show();
                sortProductsByDate(isDateDescending);
                break;
            case 1:
                Toast.makeText(getContext(), "Hey2-2", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getContext(), "Hey3", Toast.LENGTH_SHORT).show();
                sortProductsByPrice(isPriceAscending);
                break;

        }
    }

    @Override

    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override


    public void onClickProductItem(int position) {
        ProductDto productDto = productList.get(position);
        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, ProductDetailFragment.newInstance(productDto, userId));
    }

    @Override
    public void onResume() {
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.GONE);
        super.onResume();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
            String query = searchBar.getText().toString().trim();
            if (!query.isEmpty()) {
                searchString = query;
                getProductsByName();
                adapter.notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }
}