package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.OrderAdapter;
import com.nhom13.phonemart.dto.OrderDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.enums.OrderStatus;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.OnOrderActionListener;
import com.nhom13.phonemart.service.UserService;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class OrderFragment extends Fragment implements OnOrderActionListener {

    private Long userId;
    private String tabType;
    private boolean isFirstLoad = true;
    private List<OrderDto> orderDtos;
    private List<OrderDto> ordersPending;
    private OrderAdapter adapter;
    private TabLayout tabLayout;
    private RecyclerView recyclerView_product;
    private ImageView imageView_allOrder_back;

    public OrderFragment() {
    }

    public static OrderFragment newInstance(Long userId, String tabType) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        args.putString("tabType", tabType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong("userId");
            tabType = getArguments().getString("tabType");
            getUserById();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mappingUi(view);
        mappingEvent();
    }

    private void getUserById() {
        UserService userService = new UserService(requireContext());
        userService.getUserDto(userId, new GeneralCallBack<UserDto>() {
            @Override
            public void onSuccess(UserDto result) {
                if (result != null) {
                    orderDtos = new ArrayList<>(result.getOrders());

                    if (tabType.equals("None")) {
                        setAdapter(getOrderDtos(OrderStatus.PENDING));
                    } else if (tabType.equals("Shipping")) {
                        Objects.requireNonNull(tabLayout.getTabAt(2)).select();
                        setAdapter(getOrderDtos(OrderStatus.SHIPPED));
                    }
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

    private void mappingUi(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView_product = view.findViewById(R.id.recycleView_product);
        imageView_allOrder_back = view.findViewById(R.id.imageView_allOrder_back);
    }

    private void mappingEvent() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (orderDtos != null) {
                    int position = tab.getPosition();

                    switch (position) {
                        case 0:
                            setAdapter(getOrderDtos(OrderStatus.PENDING));
                            break;
                        case 1:
                            setAdapter(getOrderDtos(OrderStatus.PROCESSING));
                            break;
                        case 2:
                            setAdapter(getOrderDtos(OrderStatus.SHIPPED));
                            break;
                        case 3:
                            setAdapter(getOrderDtos(OrderStatus.DELIVERED));
                            break;
                        case 4:
                            setAdapter(getOrderDtos(OrderStatus.CANCELLED));
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imageView_allOrder_back.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    @NonNull
    private List<OrderDto> getOrderDtos(OrderStatus orderStatus) {
        ordersPending = new ArrayList<>();
        for (OrderDto orderDto : orderDtos) {
            if (orderDto.getOrderStatus() == orderStatus) {
                ordersPending.add(orderDto);
            }
        }

        // Sắp xếp theo ID tăng dần
        ordersPending.sort(Comparator.comparing(OrderDto::getId).reversed());
        return ordersPending;
    }

    private void setAdapter(List<OrderDto> orderDtos) {
        adapter = new OrderAdapter(requireContext(), orderDtos, this);
        recyclerView_product.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView_product.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClickOrder(int position) {
        OrderDto orderDto = ordersPending.get(position);

        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, OrderDetailsFragment.newInstance(userId, orderDto.getId()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            // Đánh dấu đã load lần đầu
            isFirstLoad = false;
        } else {
            // Gọi lại API khi quay lại fragment
            getUserById();
        }
    }
}