package com.nhom13.phonemart.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.phonemart.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseImageAdapter extends RecyclerView.Adapter<ChooseImageAdapter.ChooseImageViewHolder> {

    private Context context;
    private List<Uri> imageList;

    private RecyclerViewInterface recyclerViewInterface;


    public ChooseImageAdapter(Context context, List<Uri> imageList, RecyclerViewInterface recyclerViewInterface){
        this.context = context;
        this.imageList = imageList;
        this.recyclerViewInterface = recyclerViewInterface;

    }

    @NonNull
    @Override
    public ChooseImageAdapter.ChooseImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ChooseImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseImageAdapter.ChooseImageViewHolder holder, int position) {

        Uri imageUri = imageList.get(position);

        holder.imageView.setImageURI(imageUri);

        holder.imageView.setOnClickListener(v -> {
            if (recyclerViewInterface != null) {
                recyclerViewInterface.onImageClick(imageUri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ChooseImageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public ChooseImageViewHolder(@NonNull View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.chooseImg);
        }
    }
}
