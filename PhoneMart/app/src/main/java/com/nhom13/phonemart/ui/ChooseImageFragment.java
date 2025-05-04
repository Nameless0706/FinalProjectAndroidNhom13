package com.nhom13.phonemart.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.adapter.ChooseImageAdapter;
import com.nhom13.phonemart.adapter.RecyclerViewInterface;
import com.nhom13.phonemart.util.DialogUtils;
import com.nhom13.phonemart.util.FragmentUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChooseImageFragment extends Fragment implements View.OnClickListener, RecyclerViewInterface {

    private ImageView backImg, cameraImg;

    private List<Uri> imageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChooseImageAdapter adapter;
    private static final int READ_PERMISSION = 123;
    private static final int CAMERA_PERMISSION = 124;

    private static final int REQUEST_IMAGE_CAPTURE = 125;



    //private ActivityResultLauncher<String> permissionLauncher;


    public ChooseImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Mapping(view);

        backImg.setOnClickListener(this);
        cameraImg.setOnClickListener(this);

        //Hỏi quyền truy cập bộ nhớ người dùng
        askStoragePermissions();

    }

    private void askStoragePermissions(){
        if (ContextCompat.checkSelfPermission(requireContext(),
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                        Manifest.permission.READ_MEDIA_IMAGES :
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                                    Manifest.permission.READ_MEDIA_IMAGES :
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                    }, READ_PERMISSION);
        }
        else {
            loadImages();
        }
    }

    private void askCameraPermissions(){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

        } else {
            openCamera();
        }
    }



    private void Mapping(View view) {
        backImg = (ImageView) view.findViewById(R.id.chooseBackImg);
        cameraImg = (ImageView) view.findViewById(R.id.openCameraImg);
        recyclerView = (RecyclerView) view.findViewById(R.id.galleryRc);
    }

    private void loadImages() {
        imageList = getAllImages(requireActivity());
        adapter = new ChooseImageAdapter(getContext(), imageList, ChooseImageFragment.this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(adapter);
    }

    //Lấy URI của tất cả ảnh trong thư mục Pictures
    private ArrayList<Uri> getAllImages(Activity activity) {
        ArrayList<Uri> imageUris = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[]{
                MediaStore.Images.Media._ID
        };

        try (Cursor cursor = activity.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        )) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    Log.d("ImageURI", contentUri.toString());
                    imageUris.add(contentUri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageUris;
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void saveImageToGallery(Bitmap imageBitmap){
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        Log.d("TIMEEEE", timeStamp);

        String filename = "IMG_" + timeStamp + ".jpg";

        File imageFile = new File(storageDir, filename);

        try{

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            requireContext().sendBroadcast(mediaScanIntent);

            Toast.makeText(getContext(), "Đã lưu ảnh thành công", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //Các luồng dựa vào việc người dùng có cấp quyền hay không và cấp quyền cho cái gì
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadImages();
        }
        else if (requestCode == CAMERA_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
        else {
            DialogUtils.ShowDialog(requireContext(), R.layout.error_dialog, "Thất bại", "Không thể thực hiện hành động do thiếu quyền.");
        }
    }

    //Bắt kết quả trả về sau khi kết thúc activity mở camera

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            saveImageToGallery(imageBitmap);
            loadImages(); //Load lại thư viện sau khi lưu ảnh

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.chooseBackImg){
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        else{
            askCameraPermissions();

        }
    }

    @Override
    public void onImageClick(Uri imageUri){
        Bundle result = new Bundle();
        result.putString("selectedImageUri", imageUri.toString());

        //Cần implement hàm để bắt result từ Fragment này
        requireActivity().getSupportFragmentManager().setFragmentResult("image_result", result);

        //Chỗ này có thể đưa tới fragment mới để xác nhận, phòng trường hợp người dùng ấn nhầm
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        BottomNavigationView navBar = requireActivity().findViewById(R.id.bottom_nav_bar);
        navBar.setVisibility(View.GONE);
    }
}
