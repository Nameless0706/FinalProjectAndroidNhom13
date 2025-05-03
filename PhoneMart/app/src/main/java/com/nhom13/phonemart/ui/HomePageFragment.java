package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.CategoryAdapter;
import com.nhom13.phonemart.adapter.PopularProductAdapter;
import com.nhom13.phonemart.adapter.RecyclerViewInterface;
import com.nhom13.phonemart.api.CategoryAPI;
import com.nhom13.phonemart.api.ProductAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.CategoryDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment implements RecyclerViewInterface, View.OnClickListener, TextView.OnEditorActionListener {

    private static final String LOGIN_USER = "login_user";
    private ImageView cartImg;

    private TextView userNameTv, viewAllTv;
    private EditText searchStr;
    private List<CategoryDto> categoryList;
    private List<ProductDto> productList;
    private CategoryAdapter categoryAdapter;

    private PopularProductAdapter popularProductAdapter;
    private RecyclerView rvCategories, rvProducts;
    private ViewFlipper viewFlipper;
    private UserDto loginUser;

    private CategoryAPI categoryAPI;

    private ProductAPI productAPI;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance(UserDto userDto) {
        HomePageFragment fragment = new HomePageFragment();
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
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here
        Mapping(view);

        String name = loginUser.getFirstName() + " " + loginUser.getLastName();
        userNameTv.setText(name);

        getAllCategories();
        getPopularProducts();

        cartImg.setOnClickListener(this);
        searchStr.setOnEditorActionListener(this);
        viewAllTv.setOnClickListener(this);

        actionViewFlipperMain();

    }

    // Hàm Flipper
    private void actionViewFlipperMain() {
//        List<String> arrayListFlipper = new ArrayList<>();
//        arrayListFlipper.add("http://app.iotstar.vn:8081/appfoods/flipper/quangcao.png");
//        arrayListFlipper.add("http://app.iotstar.vn:8081/appfoods/flipper/coffee.jpg");
//        arrayListFlipper.add("http://app.iotstar.vn:8081/appfoods/flipper/companypizza.jpeg");
//        arrayListFlipper.add("http://app.iotstar.vn:8081/appfoods/flipper/themoingon.jpeg");

        List<Integer> arrayListFlipper = new ArrayList<>();
        arrayListFlipper.add(R.drawable.arrow);
        arrayListFlipper.add(R.drawable.profile);
        arrayListFlipper.add(R.drawable.baseline_home_24);

        for (int i = 0; i < arrayListFlipper.size(); i++) {
            ImageView imageView = new ImageView(requireContext());
            Glide.with(requireContext()).load(arrayListFlipper.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }

        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();

        // Thiết lập animation cho flipper
        Animation slide_in = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right);

        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void Mapping(View view) {
        cartImg = (ImageView) view.findViewById(R.id.imageView_cart);
        searchStr = (EditText) view.findViewById(R.id.searchEt);
        rvCategories = (RecyclerView) view.findViewById(R.id.categoryRv);
        rvProducts = (RecyclerView) view.findViewById(R.id.popularProductRv);
        userNameTv = (TextView) view.findViewById(R.id.userNameTv);
        viewAllTv = (TextView) view.findViewById(R.id.viewAllProdTv);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipper_productImage);
    }

    private void getAllCategories() {
        categoryList = new ArrayList<>();
        categoryAPI = RetrofitClient.getClient().create(CategoryAPI.class);
        categoryAPI.getAllCategories().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {

                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    Type listType = new TypeToken<List<CategoryDto>>() {
                    }.getType();
                    categoryList = gson.fromJson(json, listType);


                    for (CategoryDto category : categoryList) {
                        if (category.getImage() != null) {
                            Log.d("Imagehere", "Image: " + category.getImage().getId());
                        }
                        Log.d("Category", "ID: " + category.getId() + ", Name: " + category.getName());
                    }

                    categoryAdapter = new CategoryAdapter(getContext(), categoryList, HomePageFragment.this);
                    rvCategories.setAdapter(categoryAdapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    rvCategories.setLayoutManager(linearLayoutManager);
                } else {
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Thất bại", "Không thể load danh mục");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e("ApiError", throwable.getMessage());
            }
        });

    }

    private void getPopularProducts() {
        productList = new ArrayList<>();
        productAPI = RetrofitClient.getClient().create(ProductAPI.class);
        productAPI.getAllProducts().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {

                    Gson gson = new Gson();
                    String json = gson.toJson(response.body().getData());
                    Type listType = new TypeToken<List<ProductDto>>() {
                    }.getType();
                    productList = gson.fromJson(json, listType);

                    popularProductAdapter = new PopularProductAdapter(getContext(), productList, HomePageFragment.this);
                    rvProducts.setAdapter(popularProductAdapter);
                    LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    rvProducts.setLayoutManager(linearLayoutManager2);
                } else {
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Thất bại", "Không thể load sản phẩm");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable throwable) {
                Log.e("ApiError", throwable.getMessage());
            }
        });
    }


    @Override
    public void onItemClick(int position, String source) {
        // Note: Tao fragment bang constructor de gan cac thong tin, bay gio chi tam thoi load
        switch (source) {
            case "category":
                CategoryDto selectedCategory = categoryList.get(position);
                Toast.makeText(getContext(), "Clicked category: " + selectedCategory.getName(), Toast.LENGTH_SHORT).show();
                break;

            case "product":
                ProductDto selectedProduct = productList.get(position);
                FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, ProductDetailFragment.newInstance(selectedProduct, loginUser.getId()));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Fragment selected = null;
        if (view.getId() == R.id.imageView_cart) {
            selected = CartFragment.newInstance(loginUser.getId());
        } else {
            selected = new AllProductFragment();
        }
        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, selected);

    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
            // Handle Enter (or search) key press
            String query = searchStr.getText().toString().trim();
            if (!query.isEmpty()) {
                // You can trigger a search or filter operation here
                Toast.makeText(getContext(), "Searching for: " + query, Toast.LENGTH_SHORT).show();
                FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, new AllProductFragment());
                // For example, call your search function here or update your UI
            }
            return true; // Return true if the action is handled
        }
        return false; // Return false if action is not handled

    }
}