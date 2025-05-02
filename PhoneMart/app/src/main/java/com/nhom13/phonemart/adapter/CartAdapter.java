package com.nhom13.phonemart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.CartItemDto;
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.model.interfaces.OnCartItemActionListener;
import com.nhom13.phonemart.util.ImageUtils;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItemDto> cartItemDtos;
    private OnCartItemActionListener listener;

    public CartAdapter(Context context, List<CartItemDto> cartItemDtos, OnCartItemActionListener listener) {
        this.context = context;
        this.cartItemDtos = cartItemDtos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartAdapter.CartViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartItemDto item = cartItemDtos.get(position);
        holder.cartItemNameTv.setText(item.getProduct().getName());
        holder.cartItemPriceTv.setText(String.format("$ %s", item.getProduct().getPrice()));
        holder.cartItemQuantity.setText(String.valueOf(item.getQuantity()));
        holder.totalCartItemPriceTv.setText(String.format("$ %s", item.getTotalPrice()));

        List<ImageDto> images = item.getProduct().getImages();
        if (images != null && !images.isEmpty()) {
            ImageUtils.loadImageIntoImageView(context, images.get(0).getId(), holder.cartItemImg);
        } else {
            holder.cartItemImg.setImageResource(R.drawable.cat1); // ảnh mặc định
        }

    }

    @Override
    public int getItemCount() {
        return cartItemDtos.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cartItemNameTv, cartItemPriceTv, cartItemQuantity, totalCartItemPriceTv, button_minus, button_add;
        ImageView cartItemImg, imageView_deleteCartItem;
        OnCartItemActionListener listener;

        public CartViewHolder(@NonNull View itemView, OnCartItemActionListener listener) {
            super(itemView);
            cartItemNameTv = itemView.findViewById(R.id.cartItemNameTv);
            cartItemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            cartItemImg = itemView.findViewById(R.id.cartItemImg);
            cartItemQuantity = itemView.findViewById(R.id.itemQuantityTv);
            totalCartItemPriceTv = itemView.findViewById(R.id.totalCartItemPriceTv);
            button_add = itemView.findViewById(R.id.addBtn);
            button_minus = itemView.findViewById(R.id.minusBtn);
            imageView_deleteCartItem = itemView.findViewById(R.id.imageView_deleteCartItem);

            this.listener = listener;

            button_add.setOnClickListener(v -> {
                listener.onQuantityChangeRequested(getBindingAdapterPosition(), true);
            });

            button_minus.setOnClickListener(v -> {
                listener.onQuantityChangeRequested(getBindingAdapterPosition(), false);
            });

            imageView_deleteCartItem.setOnClickListener(v -> {
                listener.onDeleteCartItem(getBindingAdapterPosition());
            });

            cartItemNameTv.setOnClickListener(v -> listener.onClickProductItem(getBindingAdapterPosition()));
        }

    }
}
