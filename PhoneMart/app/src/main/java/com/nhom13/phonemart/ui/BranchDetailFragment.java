package com.nhom13.phonemart.ui;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.nhom13.phonemart.dto.BranchDto;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.util.FragmentUtils;
import com.nhom13.phonemart.util.ImageUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BranchDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BranchDetailFragment extends Fragment {

    private BranchDto branchDto;

    private ImageView img_backMapsFragment;
    private Button button_status;
    private ViewFlipper viewFlipper_branchesImage;
    private TextView textView_branchName, textView_address, textView_introduce, textView_openingTime, textView_phoneNumber, textView_email;

    public BranchDetailFragment() {
        // Required empty public constructor
    }

    public static BranchDetailFragment newInstance(BranchDto branchDto) {
        BranchDetailFragment fragment = new BranchDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("branchDto", branchDto);
        fragment.setArguments(args);
        return fragment;
    }

    // gọi đầu tiên, lấy branchDto
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            branchDto = (BranchDto) getArguments().getSerializable("branchDto");
        }
    }

    // gọi thứ 2 tạo view hiển thị từ file xml
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_branch_detail, container, false);
    }

    // xử lý khi view đã tạo xong
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mappingUi(view);
        mappingEvent();
        mappingData();
    }

    private void mappingEvent() {
        img_backMapsFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment mapsFragment = new MapsFragment();
                FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, mapsFragment);
            }
        });
    }

    private void mappingData() {
        if (branchDto.isStatus()){
            button_status.setText("Opening");
            button_status.setBackgroundColor(Color.GREEN);
        } else {
            button_status.setText("Close");
            button_status.setBackgroundColor(Color.RED);
        }

        setBranchImages();

        textView_branchName.setText(branchDto.getName());
        textView_introduce.setText(branchDto.getIntroduce());
        setAddress();
        textView_openingTime.setText(branchDto.getOpeningTime());
        textView_phoneNumber.setText(branchDto.getPhoneNumber());
        textView_email.setText(branchDto.getEmail());
    }

    private void setAddress() {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(branchDto.getLatitude(), branchDto.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0);

                textView_address.setText(fullAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mappingUi(View view) {
        img_backMapsFragment = view.findViewById(R.id.imageView_cart);
        button_status = view.findViewById(R.id.button_status);
        viewFlipper_branchesImage = view.findViewById(R.id.viewFlipper_productImage);
        textView_branchName = view.findViewById(R.id.textView_branchName);
        textView_address = view.findViewById(R.id.textView_address);
        textView_introduce = view.findViewById(R.id.textView_introduce);
        textView_openingTime = view.findViewById(R.id.textView_openingTime);
        textView_phoneNumber = view.findViewById(R.id.textView_phoneNumber);
        textView_email = view.findViewById(R.id.textView_email);
    }

    private void setBranchImages() {
        if (branchDto.getImages() != null){
            for (ImageDto imageDto : branchDto.getImages()){
                ImageView imageView = new ImageView(requireContext());
                ImageUtils.loadImageIntoImageView(getContext(), (long) imageDto.getId(), imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                viewFlipper_branchesImage.addView(imageView);
            }
        }

        viewFlipper_branchesImage.setFlipInterval(5000);
        viewFlipper_branchesImage.setAutoStart(true);
        viewFlipper_branchesImage.startFlipping();

        // Thiết lập animation cho flipper
        Animation slide_in = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right);

        viewFlipper_branchesImage.setInAnimation(slide_in);
        viewFlipper_branchesImage.setOutAnimation(slide_out);
    }
}