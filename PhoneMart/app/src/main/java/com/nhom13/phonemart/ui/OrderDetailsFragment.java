package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.OrderItemsAdapter;
import com.nhom13.phonemart.dto.OrderDto;
import com.nhom13.phonemart.dto.OrderItemDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.OnOrderItemActionListener;
import com.nhom13.phonemart.service.LocationService;
import com.nhom13.phonemart.service.OrderService;
import com.nhom13.phonemart.service.UserService;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsFragment extends Fragment implements OnOrderItemActionListener {

    private Long userId, orderId;
    private OrderService orderService;
    private List<OrderItemDto> orderItems;
    private ImageView imageView_order_back;
    private TextView textView_order_branchName,
            textView_order_branchAddress,
            textView_order_branchEmail,
            textView_order_branchPhone,
            textView_order_openingTime,
            textView_orderNumber,
            textView_orderDate,
            textView_order_userName,
            textView_order_address,
            textView_order_status,
            textView_order_total,
            textView_cancelOrder;
    private RecyclerView recycleView_orderDetails;

    public OrderDetailsFragment() {
    }

    public static OrderDetailsFragment newInstance(Long userId, Long orderId) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle args = new Bundle();
        args.putLong("orderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        orderService = new OrderService(requireContext());

        if (getArguments() != null) {
            orderId = getArguments().getLong("orderId");
            userId = getArguments().getLong("userId");
            getOrderById();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mappingUi(view);
        mappingEvent();
    }

    private void getOrderById() {
        orderService.getOrderById(orderId, new GeneralCallBack<OrderDto>() {
            @Override
            public void onSuccess(OrderDto result) {
                mappingData(result);
                orderItems = new ArrayList<>(result.getOrderItems());
                setAdapters(orderItems);
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mappingUi(View view) {
        imageView_order_back = view.findViewById(R.id.imageView_order_back);

        textView_order_branchName = view.findViewById(R.id.textView_order_branchName);
        textView_order_branchAddress = view.findViewById(R.id.textView_order_branchAddress);
        textView_order_branchEmail = view.findViewById(R.id.textView_order_branchEmail);
        textView_order_branchPhone = view.findViewById(R.id.textView_order_branchPhone);
        textView_order_openingTime = view.findViewById(R.id.textView_order_openingTime);
        textView_orderNumber = view.findViewById(R.id.textView_orderNumber);
        textView_orderDate = view.findViewById(R.id.textView_orderDate);
        textView_order_userName = view.findViewById(R.id.textView_order_userName);
        textView_order_address = view.findViewById(R.id.textView_order_address);
        textView_order_status = view.findViewById(R.id.textView_order_status);
        textView_order_total = view.findViewById(R.id.textView_order_total);

        textView_cancelOrder = view.findViewById(R.id.textView_cancelOrder);

        recycleView_orderDetails = view.findViewById(R.id.recycleView_orderDetails);
    }

    private void mappingEvent() {
        imageView_order_back.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        textView_cancelOrder.setOnClickListener(v -> cancelOrder());
    }

    private void cancelOrder() {
        orderService.cancelOrder(orderId, new GeneralCallBack<OrderDto>() {
            @Override
            public void onSuccess(OrderDto result) {
                mappingData(result);
                textView_order_status.setText(String.valueOf(result.getOrderStatus()));

                orderItems = new ArrayList<>(result.getOrderItems());
                setAdapters(orderItems);

                DialogUtils.ShowDialog(getContext(), R.layout.success_dialog, "Ooh No!", "Order Canceled Successfully");
            }

            @Override
            public void onError(Throwable t) {
                DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Failed!", "Could Not Cancel Order!");
            }
        });
    }

    private void mappingData(OrderDto result) {
        textView_order_branchName.setText(result.getBranch().getName());

        LocationService locationService = new LocationService(requireContext());
        String address = locationService.getAddressFromLocation(result.getBranch().getLatitude(), result.getBranch().getLongitude());
        textView_order_branchAddress.setText(address);

        textView_order_branchEmail.setText(result.getBranch().getEmail());
        textView_order_branchPhone.setText(result.getBranch().getPhoneNumber());
        textView_order_openingTime.setText(result.getBranch().getOpeningTime());
        textView_orderNumber.setText(String.valueOf(result.getId()));
        textView_orderDate.setText(String.valueOf(result.getOrderDate()));
        getFullName();
        textView_order_address.setText(result.getAddress());
        textView_order_status.setText(String.valueOf(result.getOrderStatus()));
        textView_order_total.setText(String.format("$ %s", result.getTotalAmount()));
    }

    private void getFullName() {
        UserService userService = new UserService(requireContext());
        userService.getUserDto(userId, new GeneralCallBack<UserDto>() {
            @Override
            public void onSuccess(UserDto result) {
                textView_order_userName.setText(String.format("%s %s", result.getFirstName(), result.getLastName()));
            }

            @Override
            public void onError(Throwable t) {
                Log.d("error", "onError: " + t.getMessage());
            }
        });
    }

    private void setAdapters(List<OrderItemDto> orderItems) {
        OrderItemsAdapter adapter = new OrderItemsAdapter(getContext(), orderItems, this);
        recycleView_orderDetails.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recycleView_orderDetails.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClickOrderItem(int position) {
        OrderItemDto orderItemDto = orderItems.get(position);

        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, ProductDetailFragment.newInstance(orderItemDto.getProduct(), userId));
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrderById();
    }
}