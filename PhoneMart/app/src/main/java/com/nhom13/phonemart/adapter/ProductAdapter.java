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
import com.nhom13.phonemart.dto.ProductDto;
import com.nhom13.phonemart.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    private final RecyclerViewInterface recyclerViewInterface;
    private Context context;
    private List<ProductDto> products;



    public ProductAdapter(Context context, List<ProductDto> products, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.products = products;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductDto product = products.get(position);
        holder.productNameTv.setText(product.getName());
        holder.productPriceTv.setText(String.valueOf(product.getPrice()));


        Glide.with(context)
                .load(product.getImages())
                .error(R.drawable.ic_launcher_background)
                .into(holder.productImg);




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
            productNameTv = itemView.findViewById(R.id.prodNameTv);
            productPriceTv = itemView.findViewById(R.id.prodPriceTv);
            productImg = itemView.findViewById(R.id.prodImg);


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
