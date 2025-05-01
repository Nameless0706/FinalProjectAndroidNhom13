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
import com.nhom13.phonemart.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartAdapter.CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.cartItemNameTv.setText(item.getProduct().getName());
        holder.cartItemPriceTv.setText(String.valueOf(item.getProduct().getPrice()));

        Glide.with(context)
                .load(item.getProduct().getImages())
                .error(R.drawable.ic_launcher_background)
                .into(holder.cartItemImg);

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cartItemNameTv, cartItemPriceTv, cartItemQuantity;

        ImageView cartItemImg;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemNameTv = itemView.findViewById(R.id.cartItemNameTv);
            cartItemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            cartItemImg = itemView.findViewById(R.id.cartItemImg);
            cartItemQuantity = itemView.findViewById(R.id.itemQuantityTv);

        }

    }
}
