package com.nhom13.phonemart.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.Product;
import com.nhom13.phonemart.util.ImageUtils;

import java.util.List;

public class PopularProductAdapter extends RecyclerView.Adapter<PopularProductAdapter.ProductViewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<ProductDto> products;



    public PopularProductAdapter(Context context, List<ProductDto> products, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.products = products;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_product, parent, false);
        return new ProductViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductDto product = products.get(position);
        holder.productNameTv.setText(product.getName());
        holder.productPriceTv.setText(String.valueOf(product.getPrice()));


//        if (product.getImages() != null) {
//            ImageUtils.loadImageIntoImageView(context, product.getImages().get(0).getId(), holder.productImg);
//
//        } else {
//            // Optionally load a placeholder or fallback image
//            holder.productImg.setImageResource(R.drawable.ic_launcher_background);
//        }




    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView productNameTv;

        TextView productPriceTv;

        ImageView productImg;



        public ProductViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            productNameTv = itemView.findViewById(R.id.popularProdNameTv);
            productPriceTv = itemView.findViewById(R.id.popularProdPriceTv);
            productImg = itemView.findViewById(R.id.popularProdImg);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null){
                        int position = getBindingAdapterPosition();

                        if (position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position, "product");
                        }
                    }
                }
            });

        }


    }
}
