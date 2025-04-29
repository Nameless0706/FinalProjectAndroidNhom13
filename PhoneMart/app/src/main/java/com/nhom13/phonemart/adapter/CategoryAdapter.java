package com.nhom13.phonemart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.CategoryDto;
import com.nhom13.phonemart.model.Category;
import com.nhom13.phonemart.util.ImageUtils;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<CategoryDto> categories;



    public CategoryAdapter(Context context, List<CategoryDto> categories, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.categories = categories;
        this.recyclerViewInterface = recyclerViewInterface;

    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryDto category = categories.get(position);
        holder.categoryNameTv.setText(category.getName());

        if (category.getImage() != null) {
            ImageUtils.loadImageIntoImageView(context, category.getImage().getId(), holder.cateImg);
        } else {
            // Optionally load a placeholder or fallback image
            holder.cateImg.setImageResource(R.drawable.ic_launcher_background);
        }

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTv;

        ImageView cateImg;

        public CategoryViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            categoryNameTv = itemView.findViewById(R.id.categoryNameTv);

            cateImg = itemView.findViewById(R.id.categoryImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null){
                        int position = getBindingAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position, "category");
                        }
                    }
                }
            });
        }


    }
}
