package com.nhom13.phonemart.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.CartAdapter;
import com.nhom13.phonemart.api.CartAPI;
import com.nhom13.phonemart.api.CartItemAPI;
import com.nhom13.phonemart.api.RetrofitClient;
import com.nhom13.phonemart.dto.BranchDto;
import com.nhom13.phonemart.dto.CartDto;
import com.nhom13.phonemart.dto.CartItemDto;
import com.nhom13.phonemart.dto.OrderDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.CartItem;
import com.nhom13.phonemart.model.interfaces.BranchCallback;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.LocationCallback;
import com.nhom13.phonemart.model.interfaces.OnCartItemActionListener;
import com.nhom13.phonemart.service.BranchService;
import com.nhom13.phonemart.service.CartItemService;
import com.nhom13.phonemart.service.CartService;
import com.nhom13.phonemart.service.LocationService;
import com.nhom13.phonemart.service.OrderService;
import com.nhom13.phonemart.service.PaymentService;
import com.nhom13.phonemart.service.ProductService;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CartFragment extends Fragment implements View.OnClickListener, OnCartItemActionListener {
    private CartAdapter adapter;
    private RecyclerView rvCartItems;
    private CartAPI cartAPI;
    private CartItemAPI cartItemAPI;
    private Long userId, cartId;
    private CartService cartService;

    private int paymentMethod = 1;

    private WebView vnPayWebView;
    private String cardType;


    private PaymentService paymentService;

    private ConstraintLayout paymentMethodGroup;

    private ProductService productService;
    private CartItemService cartItemService;
    private CartDto cartDto;
    private List<CartItemDto> cartItemDtos;
    private BranchDto branchDto;
    private ImageView backBtn, imageView_location;
    private TextView textView_totalProducts, textView_totalPrice, textView_paymentMethod , textView_branch;
    private EditText editText_address;
    private Button button_order;
    private boolean isFirstLoad = true;

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

        cartService = new CartService(requireContext());
        productService = new ProductService();
        paymentService = new PaymentService();
        cartItemService = new CartItemService(requireContext());

        cartAPI = RetrofitClient.getClient().create(CartAPI.class);
        cartItemAPI = RetrofitClient.getClient().create(CartItemAPI.class);

        if (getArguments() != null) {
            userId = getArguments().getLong("userId");
            getCartByUserId();
            // lấy tọa độ chuyển thành địa chỉ, lấy branch gần nhất
            getYourCurrentLocation();
        }
    }

    private void getCartByUserId() {
        cartService.getCartByUserId(userId, new GeneralCallBack<CartDto>() {
            @Override
            public void onSuccess(CartDto result) {
                if (result != null) {
                    cartDto = result;
                    handleCartResponse(result);
                } else {
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Load failure", "Please Login!");
                }
            }

            @Override
            public void onError(Throwable t) {
                //Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCartResponse(CartDto cartDto) {
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
        paymentMethodGroup.setOnClickListener(this);
        button_order.setOnClickListener(this);
    }

    private void Mapping(View view) {
        backBtn = view.findViewById(R.id.userProfileBackImg);
        imageView_location = view.findViewById(R.id.imageView_location);

        rvCartItems = view.findViewById(R.id.rvCart);

        paymentMethodGroup = view.findViewById(R.id.paymentMethodGroup);

        textView_totalProducts = view.findViewById(R.id.textView_brand);
        textView_totalPrice = view.findViewById(R.id.textView_totalPrice);
        textView_paymentMethod = view.findViewById(R.id.paymentMethodTv);
        textView_branch = view.findViewById(R.id.textView_branch);

        vnPayWebView = view.findViewById(R.id.vnPayWebView);

        editText_address = view.findViewById(R.id.editText_address);

        button_order = view.findViewById(R.id.orderBtn);
    }

    private void mappingData() {
        int count = 0;

        for (CartItemDto cartItemDto : cartItemDtos) {
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
        } else if (view.getId() == R.id.imageView_location) {
            // Yêu cầu quyền vị trí từ Fragment
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        } else if (view.getId() == R.id.paymentMethodGroup){
            showChoosePaymentMethodDialog();
        } else if (view.getId() == R.id.orderBtn) {
            placeOrder();
        }

    }

    private void showChoosePaymentMethodDialog(){
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout onReceiveLayout = dialog.findViewById(R.id.layoutOnReceive);
        LinearLayout vnPayLayout = dialog.findViewById(R.id.layoutVnPay);


        onReceiveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_paymentMethod.setText("Thanh toán khi nhận hàng");
                dialog.dismiss();
                paymentMethod = 1;

            }
        });

        vnPayLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_paymentMethod.setText("Thanh toán qua VNPay");
                dialog.dismiss();
                paymentMethod = 2;
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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
        cartItemService.updateCartItem(cartId, cartItemDto.getProduct().getId(), newQuantity, new GeneralCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                getCartByUserId();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Cập nhật UI
        cartItemDto.setQuantity(newQuantity);
        cartItemDto.setTotalPrice(cartItemDto.getUnitPrice().multiply(BigDecimal.valueOf(newQuantity)));
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onDeleteCartItem(int position) {
        CartItemDto cartItemDto = cartItemDtos.get(position);
        cartItemService.deleteCartItem(cartId, cartItemDto.getProduct().getId(), new GeneralCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                getCartByUserId();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // cập nhật UI
        cartItemDtos.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onClickProductItem(int position) {
        ProductDto productDto = cartItemDtos.get(position).getProduct();

        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, ProductDetailFragment.newInstance(productDto, userId));
    }

    // nhờ vào addToBackStack() trong loadFragment() của FragmentUtils nên khi click chuyển sang ProductDetailFragment, CartFragment vẫn còn giữ trong bộ nhớ --> các biến vẫn còn giữ giá trị --> click back thì chỉ cần gọi getCartById là có thể load lại
    @Override
    public void onResume() {
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.GONE);
        super.onResume();

        if (isFirstLoad) {
            // Đánh dấu đã load lần đầu
            isFirstLoad = false;
        } else {
            // Gọi lại API khi quay lại fragment
            getCartByUserId();
        }
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

    private void openVNPayWebView() {

        BigDecimal total = cartDto.getTotalAmount();
        paymentService.createPayment(total, new GeneralCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                loadVNPayWebView(result);
            }

            @Override
            public void onError(Throwable t) {
                DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Load failure", "Không thể lấy url!");
            }
        });

    }

    private void loadVNPayWebView(String url) {
        vnPayWebView.setVisibility(View.VISIBLE);
        vnPayWebView.getSettings().setJavaScriptEnabled(true);
        vnPayWebView.loadUrl(url);

        vnPayWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("payment-info")) {
                    handleVNPayReturn(url);
                    return true;
                }
                return false;
            }
        });
    }

    private void handleVNPayReturn(String url) {
        // Parse URL lấy thông tin kết quả
        Uri uri = Uri.parse(url);
        String responseCode = uri.getQueryParameter("vnp_ResponseCode");
        cardType = uri.getQueryParameter("vnp_CardType");

        if ("00".equals(responseCode)) {
            DialogUtils.ShowDialog(requireContext(), R.layout.success_dialog, "Thành công", "Thanh toán thành công");
        } else {
            DialogUtils.ShowDialog(requireContext(), R.layout.error_dialog, "Thất bại", "Thanh toán thất bại");
        }

        // Ẩn WebView sau khi thanh toán xong
        vnPayWebView.setVisibility(View.GONE);
        createOrder("Thanh toán qua VNPay", cardType);

    }

    private void placeOrder() {
        if (!TextUtils.isEmpty(editText_address.getText()) && branchDto != null && cartDto != null && cartItemDtos != null && !cartItemDtos.isEmpty()) {
            if (paymentMethod == 2){
               openVNPayWebView();
            }

            else{
                createOrder("Thanh toán khi nhận hàng", "");
            }
            updateSoldCount();
        }

        else{
            DialogUtils.ShowDialog(requireContext(), R.layout.error_dialog, "Thất bại", "Vui lòng điền đầy đủ thông tin");
        }
    }

    private void createOrder(String paymentMethod, String cardType){
        OrderService orderService = new OrderService(requireContext());

        String address = String.valueOf(editText_address.getText());

        orderService.placeOrder(userId, branchDto.getId(), address, paymentMethod, cardType, new GeneralCallBack<OrderDto>() {
            @Override
            public void onSuccess(OrderDto result) {
                FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, OrderDetailsFragment.newInstance(userId, result.getId()));
                DialogUtils.ShowDialog(getContext(), R.layout.success_dialog, "Thanks!", "Order placed successfully");
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSoldCount(){
        for(CartItemDto cartItemDto : cartDto.getCartItems()){
            productService.updateProductSoldCount(cartItemDto.getProduct().getId(), cartItemDto.getQuantity(), new GeneralCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    // Không cần xử lý
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}