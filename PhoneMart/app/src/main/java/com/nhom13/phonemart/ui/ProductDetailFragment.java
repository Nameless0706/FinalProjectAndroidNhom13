package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.api.CartItemAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.api.UserAPI;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.ImageUtils;
import com.nhom13.phonemart.util.TokenUtils;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailFragment extends Fragment implements View.OnClickListener {

    private UserAPI userAPI;
    private Long userId;
    private UserDto userDto;

    private static final String PRODUCT_DTO = "product_dto";
    private ProductDto productDto;
    private CartItemAPI cartItemAPI;
    private ImageView imageView_back, imageView_favourite;
    private ViewFlipper viewFlipper_productImage;
    private TextView textView_productName, textView_price, textView_brand, textView_description, textView_sold, textView_inventory, textView_category;
    private Button button_addToCart, button_buy;

    boolean isFavorite = false;

    public ProductDetailFragment() {
    }

    public static ProductDetailFragment newInstance(ProductDto productDto, Long userId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(PRODUCT_DTO, productDto);
        args.putSerializable("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cartItemAPI = RetrofitClient.getClient().create(CartItemAPI.class);
        userAPI = RetrofitClient.getClient().create(UserAPI.class);

        if (getArguments() != null) {
            productDto = (ProductDto) getArguments().getSerializable(PRODUCT_DTO);
            userId = (Long) getArguments().getSerializable("userId");

            getUserById();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here
        mappingUi(view);
        mappingData();
        imageView_back.setOnClickListener(this);
        button_addToCart.setOnClickListener(this);
        button_buy.setOnClickListener(this);
        imageView_favourite.setOnClickListener(this);
    }

    private void mappingUi(View view) {
        imageView_back = view.findViewById(R.id.imageView_back);
        imageView_favourite = view.findViewById(R.id.imageView_favourite);
        viewFlipper_productImage = view.findViewById(R.id.viewFlipper_productImage);
        textView_productName = view.findViewById(R.id.textView_productName);
        textView_price = view.findViewById(R.id.textView_price);
        textView_brand = view.findViewById(R.id.textView_brand);
        textView_sold = view.findViewById(R.id.textView_sold);
        textView_inventory = view.findViewById(R.id.textView_inventory);
        textView_category = view.findViewById(R.id.textView_category);
        textView_description = view.findViewById(R.id.textView_description);
        button_addToCart = view.findViewById(R.id.button_addToCart);
        button_buy = view.findViewById(R.id.button_buy);
    }

    private void mappingData() {
        setProductImages();

        textView_productName.setText(productDto.getName());
        textView_price.setText("$ " + productDto.getPrice());
        textView_brand.setText(productDto.getBrand());
        textView_sold.setText(String.valueOf(getRandomSold()));
        textView_inventory.setText(String.valueOf(productDto.getInventory()));
        textView_category.setText(productDto.getCategory().getName());
        textView_description.setText(productDto.getDescription());
    }

    private int getRandomSold() {
        return new Random().nextInt(productDto.getInventory() + 1);
    }

    private void setProductImages() {
        if (productDto.getImages() != null) {
            for (ImageDto imageDto : productDto.getImages()) {
                ImageView imageView = new ImageView(requireContext());
                ImageUtils.loadImageIntoImageView(getContext(), (long) imageDto.getId(), imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                viewFlipper_productImage.addView(imageView);
            }
        }

        viewFlipper_productImage.setFlipInterval(5000);
        viewFlipper_productImage.setAutoStart(true);
        viewFlipper_productImage.startFlipping();

        // Thiết lập animation cho flipper
        Animation slide_in = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right);

        viewFlipper_productImage.setInAnimation(slide_in);
        viewFlipper_productImage.setOutAnimation(slide_out);
    }

    @Override
    public void onClick(View view) {
        Fragment selected = null;
        // cả addProductToCart/buyProduct phải check viewId để xử lý response
        if (view.getId() == R.id.imageView_back) {
            requireActivity().getSupportFragmentManager().popBackStack();
        } else if (view.getId() == R.id.button_addToCart) {
            addProductToCart(view.getId());
        } else if (view.getId() == R.id.button_buy) {
            buyProduct(view.getId());
        } else if (view.getId() == R.id.imageView_favourite) {
            saveFavoriteProduct(view);
        }
    }

    private void saveFavoriteProduct(View view) {
        ImageView imageView = (ImageView) view;

        if (isFavorite) {
            imageView.setImageResource(R.drawable.baseline_favorite_border_24);
        } else {
            imageView.setImageResource(R.drawable.baseline_favorite_24);
        }

        handleSaveFavoriteProduct();

        // Đảo trạng thái
        isFavorite = !isFavorite;
    }

    private void buyProduct(int viewId) {
        addProductToCart(viewId);
    }

    private void addProductToCart(int viewId) {
        String accessToken = TokenUtils.getAccessToken(requireContext());

        cartItemAPI.addCartItem(productDto.getId(), 1, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleAddProductResponse(viewId, response);
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(requireContext());

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            addProductToCart(viewId);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.d("onFailure", "onFailure: " + throwable.getMessage());
            }
        });
    }

    private void handleAddProductResponse(int viewId, Response<ApiResponse> response) {
        if (viewId == R.id.button_addToCart) {
            Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_buy) {
            // nếu đưa thẳng vào buyProduct() thì sẽ gặp lỗi async
            FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, CartFragment.newInstance(userId));
        }
    }

    private void getUserById() {
        String accessToken = TokenUtils.getAccessToken(requireContext());

        userAPI.getUserById(userId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleGetUserResponse(response);
                } else if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(requireContext());

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            getUserById();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                            Log.d("onFailure", "onFailure: " + requireContext());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.d("onFailure", "onFailure: " + throwable.getMessage());
            }
        });
    }

    private void handleGetUserResponse(Response<ApiResponse> response) {
        Gson gson = new Gson();
        String json = gson.toJson(response.body().getData());
        userDto = gson.fromJson(json, UserDto.class);

        for (ProductDto product : userDto.getFavoriteProducts()) {
            if (product.getId().equals(productDto.getId())) {
                isFavorite = true;
                break;
            }
        }

        if (isFavorite) {
            imageView_favourite.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            imageView_favourite.setImageResource(R.drawable.baseline_favorite_border_24);
        }
    }

    private void handleSaveFavoriteProduct() {
        String accessToken = TokenUtils.getAccessToken(requireContext());

        userAPI.saveFavoriteProduct(userId, productDto.getId(), "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 401) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(requireContext());

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            handleSaveFavoriteProduct();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                            Log.d("onFailure", "onFailure: " + requireContext());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.d("onFailure", "onFailure: " + throwable.getMessage());
            }
        });
    }
}