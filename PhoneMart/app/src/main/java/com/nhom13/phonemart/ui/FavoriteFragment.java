package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.FavoriteProductsAdapter;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.interfaces.GeneralCallBack;
import com.nhom13.phonemart.model.interfaces.OnProductItemActionListener;
import com.nhom13.phonemart.service.UserService;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment implements OnProductItemActionListener {

    private Long userId;
    private UserDto userDto;
    private List<ProductDto> productDtos;
    private TextView textView_userName;
    private ImageView imageView_cart;
    private SearchView searchView_favoriteProduct;
    private RecyclerView recyclerView_favoriteProducts;
    private FavoriteProductsAdapter favoriteProductsAdapter;

    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance(Long userId) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong("userId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mappingUi(view);
        mappingEvent();
        getUserDto();
    }

    private void mappingUi(View view) {
        textView_userName = view.findViewById(R.id.textView_userName);
        imageView_cart = view.findViewById(R.id.imageView_cart);
        searchView_favoriteProduct = view.findViewById(R.id.searchView_favoriteProduct);
        recyclerView_favoriteProducts = view.findViewById(R.id.recyclerView_favoriteProducts);
    }

    private void mappingEvent() {
        imageView_cart.setOnClickListener(v -> redirectCartFragment());

        searchView_favoriteProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterListener(newText);
                return false;
            }
        });
    }

    private void redirectCartFragment() {
        Fragment selected = CartFragment.newInstance(userId);

        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, selected);
    }

    private void filterListener(String newText) {
        List<ProductDto> list = new ArrayList<>();
        if (productDtos != null) {
            for (ProductDto productDto : productDtos) {
                if (productDto.getName().toLowerCase().contains(newText.toLowerCase()) || productDto.getDescription().toLowerCase().contains(newText.toLowerCase())) {
                    list.add(productDto);
                }
            }
        }

        if (!list.isEmpty()) {
            favoriteProductsAdapter.setListenerList(list);
        }
    }

    private void getUserDto() {
        UserService userService = new UserService(requireContext());
        userService.getUserDto(userId, new GeneralCallBack<UserDto>() {
            @Override
            public void onSuccess(UserDto result) {
                if (result != null) {
                    userDto = result;
                    productDtos = new ArrayList<>(result.getFavoriteProducts());

                    textView_userName.setText(String.format("%s %s", result.getFirstName(), result.getLastName()));

                    setAdapters(productDtos);
                } else {
                    DialogUtils.ShowDialog(getContext(), R.layout.error_dialog, "Load failure", "Please Login!");
                }
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setAdapters(List<ProductDto> productDtos) {
        favoriteProductsAdapter = new FavoriteProductsAdapter(requireContext(), productDtos, this);
        recyclerView_favoriteProducts.setAdapter(favoriteProductsAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false);
        recyclerView_favoriteProducts.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onClickProductItem(int position) {
        ProductDto productDto = productDtos.get(position);
        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, ProductDetailFragment.newInstance(productDto, userId));
    }

    @Override
    public void onResume() {
        super.onResume();

        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.VISIBLE);
    }
}