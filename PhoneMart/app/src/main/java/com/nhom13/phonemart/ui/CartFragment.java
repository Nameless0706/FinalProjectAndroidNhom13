package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.CartAdapter;
import com.nhom13.phonemart.model.CartItem;
import com.nhom13.phonemart.model.Category;
import com.nhom13.phonemart.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements View.OnClickListener{

    private ImageView backBtn;
    private List<CartItem> cartItems;

    private CartAdapter adapter;

    private RecyclerView rvCartItems;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        getAllCartItems();
        setAdapters();
        backBtn.setOnClickListener(this);


    }

    private void Mapping(View view) {
        backBtn = view.findViewById(R.id.userProfileBackImg);
        rvCartItems = view.findViewById(R.id.rvCart);
    }



    private void getAllCartItems(){
        cartItems = new ArrayList<>();
    }

    private void setAdapters(){
        adapter = new CartAdapter(getContext(), cartItems);
        rvCartItems.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvCartItems.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.userProfileBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        else if (view.getId() == R.id.orderBtn){

        }
        else{

        }
    }
}