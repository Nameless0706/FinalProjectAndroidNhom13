package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.CategoryAdapter;
import com.nhom13.phonemart.adapter.PopularProductAdapter;
import com.nhom13.phonemart.adapter.RecyclerViewInterface;
import com.nhom13.phonemart.model.Category;
import com.nhom13.phonemart.model.Product;
import com.nhom13.phonemart.util.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment implements RecyclerViewInterface, View.OnClickListener, TextView.OnEditorActionListener {

    ImageView cartImg;

    TextView viewAllTv;
    EditText searchStr;
    List<Category> categoryList;
    List<Product> productList;
    CategoryAdapter categoryAdapter;

    PopularProductAdapter popularProductAdapter;
    RecyclerView rvCategories, rvProducts;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomePageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomePageFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static HomePageFragment newInstance(String param1, String param2) {
        HomePageFragment fragment = new HomePageFragment();
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
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize your views or setup listeners here
        Mapping(view);
        getAllCategories();
        getPopularProducts();
        setAdapters();

        cartImg.setOnClickListener(this);
        searchStr.setOnEditorActionListener(this);
        viewAllTv.setOnClickListener(this);

    }


    private void Mapping(View view) {
        cartImg = (ImageView) view.findViewById(R.id.cartImg);
        searchStr = (EditText) view.findViewById(R.id.searchEt);
        rvCategories = (RecyclerView) view.findViewById(R.id.categoryRv);
        rvProducts = (RecyclerView) view.findViewById(R.id.popularProductRv);
        viewAllTv = (TextView) view.findViewById(R.id.viewAllProdTv);
    }

    private void getAllCategories(){
        categoryList = new ArrayList<>();

    }

    private void getPopularProducts(){
        productList = new ArrayList<>();

    }

    private void setAdapters(){
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        rvCategories.setAdapter(categoryAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCategories.setLayoutManager(linearLayoutManager);

        popularProductAdapter = new PopularProductAdapter(getContext(), productList, this);
        rvProducts.setAdapter(popularProductAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvProducts.setLayoutManager(linearLayoutManager2);


    }


    @Override
    public void onItemClick(int position) {
        // Note: Tao fragment bang constructor de gan cac thong tin, bay gio chi tam thoi load
        FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.main_frag_container, new ProductDetailFragment());
    }

    @Override
    public void onClick(View view) {
        Fragment selected = null;
        if (view.getId() == R.id.cartImg){
            selected = new CartFragment();
        }
        else {
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