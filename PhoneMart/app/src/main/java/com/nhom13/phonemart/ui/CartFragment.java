package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.CartAdapter;
import com.nhom13.phonemart.api.CartAPI;
import com.nhom13.phonemart.api.CartItemAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.CartDto;
import com.nhom13.phonemart.dto.CartItemDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.interfaces.OnCartItemActionListener;
import com.nhom13.phonemart.model.interfaces.TokenCallback;
import com.nhom13.phonemart.model.response.ApiResponse;
import com.nhom13.phonemart.model.response.JwtResponse;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.TokenUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements View.OnClickListener, OnCartItemActionListener {
    private ImageView backBtn;
    private List<CartItemDto> cartItemDtos;
    private CartAdapter adapter;
    private RecyclerView rvCartItems;
    private CartAPI cartAPI;
    private CartItemAPI cartItemAPI;
    private Long userId;
    private Long cartId;
    private CartDto cartDto;
    private TextView textView_totalProducts, textView_totalPrice, textView_address, textView_branch;

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
                else if (response.code() == 404){
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

//    private void getCartById(Long cartId) {
//        String accessToken = TokenUtils.getAccessToken(requireContext());
//
//        cartAPI.getCart(cartId, "Bearer " + accessToken).enqueue(new Callback<ApiResponse>() {
//            @Override
//            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    handleCartResponse(response);
//                } else if (response.code() == 401) {
//                    // Token hết hạn → gọi refresh
//                    String refreshToken = TokenUtils.getRefreshToken(requireContext());
//
//                    TokenUtils.createNewAccessToken(refreshToken, new TokenCallback() {
//                        @Override
//                        public void onSuccess(JwtResponse jwtResponse) {
//                            // lưu lại token mới
//                            TokenUtils.saveTokens(requireContext(), jwtResponse.getAccessToken(), jwtResponse.getRefreshToken());
//                            // gọi lại API với token mới
//                            getCartById(cartId);
//                        }
//
//                        @Override
//                        public void onFailure(String errorMessage) {
//                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
//                        }
//                    });
//                } else {
//                    try {
//                        Log.d("error", "onResponse: " + response.errorBody().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse> call, Throwable throwable) {
//                Log.d("onFailure", "onFailure: " + throwable.getMessage());
//            }
//        });
//    }

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
    }

    private void Mapping(View view) {
        backBtn = view.findViewById(R.id.userProfileBackImg);
        rvCartItems = view.findViewById(R.id.rvCart);

        textView_totalProducts = view.findViewById(R.id.textView_brand);
        textView_totalPrice = view.findViewById(R.id.textView_totalPrice);
        textView_address = view.findViewById(R.id.textView_address);
        textView_branch = view.findViewById(R.id.textView_branch);
    }

    private void mappingData(){
        int count = 0;
        for (CartItemDto cartItemDto : cartItemDtos){
            count += cartItemDto.getQuantity();
        }

        textView_totalProducts.setText(String.valueOf(count));
        textView_totalPrice.setText(String.format("$ %s", cartDto.getTotalAmount()));
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
        } else if (view.getId() == R.id.orderBtn) {

        } else {

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
        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, ProductDetailFragment.newInstance(productDto, userId));
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
        super.onResume();
        getCartByUserId();
    }

}