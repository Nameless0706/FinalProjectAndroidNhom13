package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.ProductAdapter;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.OnProductItemActionListener;
import com.nhom13.phonemart.service.CartItemService;
import com.nhom13.phonemart.service.ProductService;
import com.nhom13.phonemart.service.UserService;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.ImageUtils;

import java.util.List;
import java.util.Random;

public class ProductDetailFragment extends Fragment implements View.OnClickListener, OnProductItemActionListener {

    private Long userId;
    private CartItemService cartItemService;
    private UserService userService;
    private static final String PRODUCT_DTO = "product_dto";
    private ProductDto productDto;
    private ImageView imageView_back, imageView_favourite;
    private ViewFlipper viewFlipper_productImage;
    private TextView textView_productName, textView_price, textView_brand, textView_description, textView_sold, textView_inventory, textView_category;
    private Button button_addToCart, button_buy;
    boolean isFavorite = false;
    private RecyclerView recyclerView_relatedProducts;
    private List<ProductDto> productDtos;

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

        cartItemService = new CartItemService(requireContext());
        userService = new UserService(requireContext());

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

        recyclerView_relatedProducts = view.findViewById(R.id.recyclerView_relatedProducts);
    }

    private void mappingData() {
        setProductImages();

        textView_productName.setText(productDto.getName());
        textView_price.setText(String.format("$ %s", productDto.getPrice()));
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
        // cả addProductToCart/buyProduct phải check viewId để xử lý response
        if (view.getId() == R.id.imageView_back) {
            requireActivity().getSupportFragmentManager().popBackStack();
        } else if (view.getId() == R.id.button_addToCart) {
            cartItemService.addProductToCart(view.getId(), productDto.getId(), new GeneralCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                    } else {
                        DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Load failure", "Please Login!");
                    }
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (view.getId() == R.id.button_buy) {
            buyProduct(view.getId());
        } else if (view.getId() == R.id.imageView_favourite) {
            saveFavoriteProduct(view);
        }
    }

    private void saveFavoriteProduct(View view) {
        ImageView imageView = (ImageView) view;

        userService.handleSaveFavoriteProduct(userId, productDto.getId(), new GeneralCallBack<UserDto>() {
            @Override
            public void onSuccess(UserDto result) {
                if (result != null) {
                    if (isFavorite) {
                        imageView.setImageResource(R.drawable.baseline_favorite_border_24);
                    } else {
                        imageView.setImageResource(R.drawable.baseline_favorite_24);
                    }

                    // Đảo trạng thái
                    isFavorite = !isFavorite;
                } else {
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Load failure", "Please Login!");
                }
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void buyProduct(int viewId) {
        cartItemService.addProductToCart(viewId, productDto.getId(), new GeneralCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    if (viewId == R.id.button_addToCart) {
                        Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                    } else if (viewId == R.id.button_buy) {
                        // nếu đưa thẳng vào buyProduct() thì sẽ gặp lỗi async
                        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, CartFragment.newInstance(userId));
                    }
                } else {
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Load failure", "Please Login!");
                }
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserById() {
        userService.getUserDto(userId, new GeneralCallBack<UserDto>() {
            @Override
            public void onSuccess(UserDto result) {
                if (result != null) {
                    for (ProductDto product : result.getFavoriteProducts()) {
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
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRelatedProducts() {
        ProductService productService = new ProductService();
        productService.getRelatedProducts(productDto.getCategory().getName(), new GeneralCallBack<List<ProductDto>>() {
            @Override
            public void onSuccess(List<ProductDto> result) {
                productDtos = result;
                setAdapter(result);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(List<ProductDto> productDtos){
        ProductAdapter adapter = new ProductAdapter(requireContext(), productDtos, this);
        recyclerView_relatedProducts.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_relatedProducts.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();

        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.GONE);

        getRelatedProducts();
    }

    @Override
    public void onClickProductItem(int position) {
        ProductDto productDto = productDtos.get(position);
        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, ProductDetailFragment.newInstance(productDto, userId));
    }
}