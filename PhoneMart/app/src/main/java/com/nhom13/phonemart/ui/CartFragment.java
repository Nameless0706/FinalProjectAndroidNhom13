package com.nhom13.phonemart.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.CartAdapter;
import com.nhom13.phonemart.api.CartAPI;
import com.nhom13.phonemart.api.CartItemAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.BranchDto;
import com.nhom13.phonemart.dto.CartDto;
import com.nhom13.phonemart.dto.CartItemDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.interfaces.BranchCallback;
import com.nhom13.phonemart.model.interfaces.LocationCallback;
import com.nhom13.phonemart.model.interfaces.OnCartItemActionListener;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.service.BranchService;
import com.nhom13.phonemart.service.LocationService;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.TokenUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements View.OnClickListener, OnCartItemActionListener {
    private CartAdapter adapter;
    private RecyclerView rvCartItems;
    private CartAPI cartAPI;
    private CartItemAPI cartItemAPI;
    private Long userId, cartId;
    private CartDto cartDto;
    private List<CartItemDto> cartItemDtos;
    private BranchDto branchDto;
    private ImageView backBtn, imageView_location;
    private TextView textView_totalProducts, textView_totalPrice, textView_branch;
    private EditText editText_address;

    private final int UNAUTHORIZE_CODE = 401;


    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance(Long userId) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cartAPI = RetrofitClient.getClient().create(CartAPI.class);
        cartItemAPI = RetrofitClient.getClient().create(CartItemAPI.class);

        if (getArguments() != null) {
            userId = getArguments().getLong("userId");
            getCartByUserId();
        }
    }

    private void getCartByUserId() {
        String accessToken = TokenUtils.getAccessToken(requireContext());

        cartAPI.getCartByUserId(userId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleCartResponse(response);

                } else if (response.code() == UNAUTHORIZE_CODE) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(requireContext());

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            getCartByUserId();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                // cart rỗng nên bên api ném exception và chưa kịp trả về ApiResponse --> body null

                else if (response.code() == 404) {
                    try {
                        Log.d("error", "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.d("onFailure", "onFailure: " + throwable.getMessage());
            }
        });
    }


    private void handleCartResponse(Response<ApiResponse> response) {
        Gson gson = new Gson();
        String json = gson.toJson(response.body().getData());
        cartDto = gson.fromJson(json, CartDto.class);

        // khi chuyển qua cartFragment và cart đã có sẵn thì gán vào cartId để update/delete cartItem
        cartId = cartDto.getId();

        cartItemDtos = new ArrayList<>(cartDto.getCartItems());
        // sort theo totalPrice
        cartItemDtos.sort(Comparator.comparing(
                item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        ));

        // map cho textView_totalProducts/textView_totalPrice
        mappingData();

        setAdapters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here
        Mapping(view);
        backBtn.setOnClickListener(this);
        imageView_location.setOnClickListener(this);
    }

    private void Mapping(View view) {
        backBtn = view.findViewById(R.id.userProfileBackImg);
        imageView_location = view.findViewById(R.id.imageView_location);

        rvCartItems = view.findViewById(R.id.rvCart);

        textView_totalProducts = view.findViewById(R.id.textView_brand);
        textView_totalPrice = view.findViewById(R.id.textView_totalPrice);
        textView_branch = view.findViewById(R.id.textView_branch);

        editText_address = view.findViewById(R.id.editText_address);
    }


    private void mappingData() {
        int count = 0;

        for (CartItemDto cartItemDto : cartItemDtos) {
            count += cartItemDto.getQuantity();
        }

        textView_totalProducts.setText(String.valueOf(count));
        textView_totalPrice.setText(String.format("$ %s", cartDto.getTotalAmount()));

        // lấy tọa độ chuyển thành địa chỉ, lấy branch gần nhất
        getYourCurrentLocation();
    }

    private void setAdapters() {
        adapter = new CartAdapter(getContext(), cartItemDtos, this);
        rvCartItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvCartItems.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.userProfileBackImg) {
            requireActivity().getSupportFragmentManager().popBackStack();

        } else if (view.getId() == R.id.imageView_location) {
            // Yêu cầu quyền vị trí từ Fragment
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        }
    }

    @Override
    public void onQuantityChangeRequested(int position, boolean isIncrement) {
        CartItemDto cartItemDto = cartItemDtos.get(position);
        int currentQuantity = cartItemDto.getQuantity();
        int newQuantity = isIncrement ? currentQuantity + 1 : currentQuantity - 1;
        int inventory = cartItemDto.getProduct().getInventory();

        if (newQuantity <= 0) {
            DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Decrease Failed", "You can choose delete cart item.");
            return;
        }

        // logic ko ổn lắm
        if (isIncrement && newQuantity > inventory) {
            DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Increase Failed", "Exceed the allowable limit.");
            return;
        }

        // Gọi API cập nhật
        updateCartItem(cartItemDto.getProduct().getId(), newQuantity);

        // Cập nhật UI
        cartItemDto.setQuantity(newQuantity);
        cartItemDto.setTotalPrice(cartItemDto.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onDeleteCartItem(int position) {
        CartItemDto cartItemDto = cartItemDtos.get(position);
        deleteCartItem(cartItemDto.getProduct().getId());

        // cập nhật UI
        cartItemDtos.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onClickProductItem(int position) {
        ProductDto productDto = cartItemDtos.get(position).getProduct();

        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, ProductDetailFragment.newInstance(productDto, userId));
    }

    private void updateCartItem(Long productId, int quantity) {
        String accessToken = TokenUtils.getAccessToken(requireContext());

        cartItemAPI.updateCartItem(cartId, productId, quantity, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.code() == UNAUTHORIZE_CODE) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(requireContext());

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            updateCartItem(productId, quantity);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                // call api để lấy lại danh sách và cập nhật totalProducts/totalPrice
                getCartByUserId();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.d("onFailure", "onFailure: " + throwable.getMessage());
            }
        });
    }

    private void deleteCartItem(Long productId) {
        String accessToken = TokenUtils.getAccessToken(requireContext());

        cartItemAPI.deleteCartItem(cartId, productId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.code() == UNAUTHORIZE_CODE) {
                    // Token hết hạn → gọi refresh
                    String refreshToken = TokenUtils.getRefreshToken(requireContext());

                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
                        @Override
                        public void onSuccess(JwtResponse jwtResponse) {
                            // lưu lại token mới
                            TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
                            // gọi lại API với token mới
                            deleteCartItem(productId);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                // cập nhật lại UI
                getCartByUserId();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
                Log.d("onFailure", "onFailure: " + throwable.getMessage());
            }
        });
    }

    // nhờ vào addToBackStack() trong loadFragment() của FragmentUtils nên khi click chuyển sang ProductDetailFragment, CartFragment vẫn còn giữ trong bộ nhớ --> các biến vẫn còn giữ giá trị --> click back thì chỉ cần gọi getCartById là có thể load lại
    @Override
    public void onResume() {
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.GONE);
        super.onResume();
        getCartByUserId();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Đã được cấp quyền, gọi lấy vị trí
            getYourCurrentLocation();
        } else {
            Toast.makeText(getContext(), "Bạn chưa cấp quyền truy cập vị trí", Toast.LENGTH_SHORT).show();
        }
    }

    private void getYourCurrentLocation() {
        LocationService locationService = new LocationService(requireContext());
        locationService.getYourCurrentLocation(new LocationCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                editText_address.setText(locationService.getAddressFromLocation(latitude, longitude));
                getBranchDto(latitude, longitude);
            }

            @Override
            public void onError(String message) {
                Log.d("error", "onError: " + message);
            }
        });
    }

    private void getBranchDto(double latitude, double longitude) {
        BranchService service = new BranchService();
        service.getAllBranches(new BranchCallback() {
            @Override
            public void onSuccess(List<BranchDto> branches) {
                branchDto = service.findNearestBranch(latitude, longitude, branches);
                textView_branch.setText(branchDto.getName());
            }

            @Override
            public void onError(Throwable t) {
                Log.d("error", "onError: " + t.getMessage());
            }
        });
    }
}