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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.CategoryAdapter;
import com.nhom13.phonemart.adapter.PopularProductAdapter;
import com.nhom13.phonemart.adapter.RecyclerViewInterface;
import com.nhom13.phonemart.dto.UserDto;
import com.nhom13.phonemart.model.Category;
import com.nhom13.phonemart.model.Product;
import com.nhom13.phonemart.util.FragmentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePageFragment extends Fragment implements RecyclerViewInterface, View.OnClickListener, TextView.OnEditorActionListener {

    ImageView cartImg;

    TextView userNameTv, viewAllTv;
    EditText searchStr;
    List<Category> categoryList;
    List<Product> productList;
    CategoryAdapter categoryAdapter;

    PopularProductAdapter popularProductAdapter;
    RecyclerView rvCategories, rvProducts;
    private ViewFlipper viewFlipper;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private UserDto loginUser;

    public HomePageFragment() {
        // Required empty public constructor
    }

    public static HomePageFragment newInstance(UserDto userDto) {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putParcelable("login_user", userDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loginUser = getArguments().getParcelable("login_user");
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

        String name = loginUser.getFirstName() + " " + loginUser.getLastName();
        userNameTv.setText(name);

        getAllCategories();
        getPopularProducts();
        setAdapters();

        cartImg.setOnClickListener(this);
        searchStr.setOnEditorActionListener(this);
        viewAllTv.setOnClickListener(this);

        ActionViewFlipperMain();

    }
    // Hàm Flipper
    private void ActionViewFlipperMain() {
//        List<String> arrayListFlipper = new ArrayList<>();
//        arrayListFlipper.add("http://app.iotstar.vn:8081/appfoods/flipper/quangcao.png");
//        arrayListFlipper.add("http://app.iotstar.vn:8081/appfoods/flipper/coffee.jpg");
//        arrayListFlipper.add("http://app.iotstar.vn:8081/appfoods/flipper/companypizza.jpeg");
//        arrayListFlipper.add("http://app.iotstar.vn:8081/appfoods/flipper/themoingon.jpeg");

        List<Integer> arrayListFlipper = new ArrayList<>();
        arrayListFlipper.add(R.drawable.arrow);
        arrayListFlipper.add(R.drawable.profile);
        arrayListFlipper.add(R.drawable.baseline_home_24);

        for (int i = 0; i < arrayListFlipper.size(); i++) {
            ImageView imageView = new ImageView(requireContext());
            Glide.with(requireContext()).load(arrayListFlipper.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }

        viewFlipper.setFlipInterval(5000);
        viewFlipper.setAutoStart(true);
        viewFlipper.startFlipping();

        // Thiết lập animation cho flipper
        Animation slide_in = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right);

        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void Mapping(View view) {
        cartImg = (ImageView) view.findViewById(R.id.cartImg);
        searchStr = (EditText) view.findViewById(R.id.searchEt);
        rvCategories = (RecyclerView) view.findViewById(R.id.categoryRv);
        rvProducts = (RecyclerView) view.findViewById(R.id.popularProductRv);
        userNameTv = (TextView) view.findViewById(R.id.userNameTv);
        viewAllTv = (TextView) view.findViewById(R.id.viewAllProdTv);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewFlipperMain);
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