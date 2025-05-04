package com.nhom13.phonemart.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.BranchDto;
import com.nhom13.phonemart.model.interfaces.BranchCallback;
import com.nhom13.phonemart.service.BranchService;
import com.nhom13.phonemart.util.FragmentUtils;

import java.util.List;
import java.util.Objects;

public class MapsFragment extends Fragment {

    private GoogleMap mMap;
    private List<BranchDto> branchDtos;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;

            // Tạo builder để tính toán vùng bao quanh tất cả marker (giới hạn bản đồ)
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

            // Duyệt qua danh sách các vị trí và thêm marker lên bản đồ
            for (BranchDto branchDto : branchDtos) {
                LatLng latLng = new LatLng(branchDto.getLatitude(), branchDto.getLongitude());

                // Tạo marker với thông tin vị trí và màu cam
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Lat: " + branchDto.getLatitude() + ", Lon: " + branchDto.getLongitude())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                // Thêm marker lên bản đồ và setTag là branchDto
                Objects.requireNonNull(mMap.addMarker(markerOptions)).setTag(branchDto);

                // Đưa LatLng vào bounds để tính vùng bao phủ camera
                boundsBuilder.include(latLng);
            }

            // Nếu có ít nhất 1 vị trí, điều chỉnh camera để bao phủ tất cả marker
            if (!branchDtos.isEmpty()) {
                // Tạo giới hạn bản đồ từ builder
                LatLngBounds bounds = boundsBuilder.build();
                // Pixel để tránh marker sát mép bản đồ
                int padding = 100;
                // Di chuyển camera tới vùng bao quanh tất cả marker
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            }

            mMap.setOnMarkerClickListener(marker -> {
                BranchDto clickedBranch = (BranchDto) marker.getTag();

                if (clickedBranch != null) {
                    // Chuyển sang Fragment và truyền BranchDto
                    BranchDetailFragment fragment = BranchDetailFragment.newInstance(clickedBranch);

                    FragmentUtils.loadFragment(requireActivity().getSupportFragmentManager(), R.id.base_frag_container, fragment);
                }

                // true nghĩa là đã thực hiện xong thao tác và Maps ko thực hiện j thêm, false nghĩa là sẽ Maps sẽ thực hiện thêm 1 số hành động mặc định như Zoom, xuất hộp thoại của marker
                return true;
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        getBranchDtos();

        // tạo View từ file xml
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    private void getBranchDtos() {
        BranchService service = new BranchService();
        service.getAllBranches(new BranchCallback() {
            @Override
            public void onSuccess(List<BranchDto> branches) {
                branchDtos = branches;
            }

            @Override
            public void onError(Throwable t) {
                Log.d("error", "onError: " + t.getMessage());
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // ánh xạ widget map cho SupportMapFragment
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

}