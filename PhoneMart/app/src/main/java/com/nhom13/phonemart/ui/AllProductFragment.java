package com.nhom13.phonemart.ui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.ProductAdapter;
import com.nhom13.phonemart.adapter.RecyclerViewInterface;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.interfaces.OnProductItemActionListener;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.viewmodel.ProductViewModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AllProductFragment extends Fragment implements View.OnClickListener, TabLayout.OnTabSelectedListener, OnProductItemActionListener, TextView.OnEditorActionListener {

    private EditText searchBar;
    private ImageView backImg, filterImg;
    private RecyclerView rvProduct;
    private ProductAdapter adapter;
    private TabLayout tabLayout;
    private List<ProductDto> productList;
    private List<ProductDto> originalProductList;
    private ProductViewModel viewModel;
    private Long userId;
    private String searchString;
    private String categoryName;
    private static final String USER_ID = "user_id";
    private static final String SEARCH_STRING = "search_string";
    private static final String CATEGORY_NAME = "category_name";
    private boolean isPriceAscending = true;
    private boolean isDateDescending = true;
    private boolean isSoldCountDescending = true;

    private boolean isDataLoaded = false;

    public AllProductFragment() {
        // Required empty public constructor
    }

    public static AllProductFragment newInstance(Long userId, String searchString, String categoryName) {
        AllProductFragment fragment = new AllProductFragment();
        Bundle args = new Bundle();
        args.putLong(USER_ID, userId);
        args.putString(SEARCH_STRING, searchString);
        args.putString(CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(USER_ID);
            searchString = getArguments().getString(SEARCH_STRING);
            categoryName = getArguments().getString(CATEGORY_NAME);
        }
        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Mapping(view);

        if (!viewModel.hasData()) {
            if (categoryName != null) {
                viewModel.getProductsByCategory(categoryName);
            } else if (searchString != null) {
                viewModel.getProductsByName(searchString);
            } else {
                viewModel.getAllProducts();
            }
        }

        viewModel.getProductList().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {

                productList = products;

                if (!isDataLoaded) {
                    originalProductList = new ArrayList<>(products);
                    productList = new ArrayList<>(originalProductList);
                    isDataLoaded = true;

                }
                setAdapter();
                getParentFragmentManager().setFragmentResultListener("priceFilter", this, (requestKey, bundle) -> {
                    applyPriceFilter(bundle);
                });
            } else {
                Log.d("AllProductFragment", "Product list is null");
            }
        });

        tabLayout.addOnTabSelectedListener(this);
        searchBar.setOnEditorActionListener(this);
        backImg.setOnClickListener(this);
        filterImg.setOnClickListener(this);
        searchBar.setText(searchString);
    }

    public void Mapping(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        rvProduct = view.findViewById(R.id.recycleView_product);
        backImg = view.findViewById(R.id.allProductBackImg);
        filterImg = view.findViewById(R.id.allProductFilterImg);
        searchBar = view.findViewById(R.id.allProductSearchEt);
    }

    private void setAdapter() {
        adapter = new ProductAdapter(getContext(), productList, AllProductFragment.this);
        rvProduct.setAdapter(adapter);
        int spanCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
        rvProduct.setLayoutManager(gridLayoutManager);
    }

    private void applyPriceFilter(Bundle bundle) {
        if (originalProductList == null || originalProductList.isEmpty()) {
            Log.d("Filter Error", "Original product list is empty or null");
            return;
        }

        productList.clear();
        String minPriceStr = bundle.getString("minPrice");
        String maxPriceStr = bundle.getString("maxPrice");

        BigDecimal minPrice = minPriceStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(minPriceStr);
        BigDecimal maxPrice = maxPriceStr.isEmpty() ? new BigDecimal("999999999") : new BigDecimal(maxPriceStr);

        for (ProductDto product : originalProductList) {
            BigDecimal price = product.getPrice();
            if (price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0) {
                productList.add(product);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void sortProductsByDate(boolean descending) {
        if (productList == null || productList.isEmpty()) {
            Log.d("Sort", "Product list is empty or null");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            productList.sort((p1, p2) -> {
                try {
                    LocalDateTime date1 = LocalDateTime.parse(p1.getDateAdded());
                    LocalDateTime date2 = LocalDateTime.parse(p2.getDateAdded());
                    return descending ? date2.compareTo(date1) : date1.compareTo(date2);
                } catch (Exception e) {
                    Log.e("Sort Error", "Could not parse date: " + e.getMessage());
                    return 0;
                }
            });
        }
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
                return ascending ? price1.compareTo(price2) : price2.compareTo(price1);
            } catch (Exception e) {
                Log.e("Sort Error", "Could not compare prices: " + e.getMessage());
                return 0;
            }
        });
        isPriceAscending = !isPriceAscending;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void sortProductsBySoldCount(boolean ascending) {
        if (productList == null || productList.isEmpty()) {
            Log.d("Sort", "Product list is empty or null");
            return;
        }
        productList.sort((p1, p2) -> {
            try {
                Integer soldCount1 = p1.getSoldCount();
                Integer soldCount2 = p2.getSoldCount();
                return ascending ? soldCount1.compareTo(soldCount2) : soldCount2.compareTo(soldCount1);
            } catch (Exception e) {
                Log.e("Sort Error", "Could not compare sold count: " + e.getMessage());
                return 0;
            }
        });
        isSoldCountDescending = !isSoldCountDescending;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.allProductBackImg) {
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, ProductFilterFragment.newInstance(userId, searchString));
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                sortProductsByDate(isDateDescending);
                break;
            case 1:
                sortProductsBySoldCount(isSoldCountDescending);
                break;
            case 2:
                sortProductsByPrice(isPriceAscending);
                break;

        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        onTabSelected(tab);
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
                viewModel.getProductsByName(searchString);

            }
            return true;
        }
        return false;
    }
}