package com.nhom13.phonemart.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.util.ImageUtils;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailFragment extends Fragment implements View.OnClickListener{

    private static final String PRODUCT_DTO = "product_dto";
    private ProductDto productDto;
    private ImageView imageView_back, imageView_favourite;
    private ViewFlipper viewFlipper_productImage;
    private TextView textView_productName, textView_price, textView_brand, textView_description, textView_sold, textView_inventory, textView_category;
    private Button button_addToCart, button_buy;

    public ProductDetailFragment() { }

    public static ProductDetailFragment newInstance(ProductDto productDto) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(PRODUCT_DTO, productDto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productDto = (ProductDto) getArguments().getSerializable(PRODUCT_DTO);
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
    }

    private void mappingData() {
        setProductImages();

        textView_productName.setText(productDto.getName());
        textView_price.setText("$ " + productDto.getPrice());
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
        if (productDto.getImages() != null){
            for (ImageDto imageDto : productDto.getImages()){
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
        Fragment selected = null;
        if (view.getId() == R.id.imageView_back){
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        else if (view.getId() == R.id.button_addToCart){

        }
        else{

        }
    }
}