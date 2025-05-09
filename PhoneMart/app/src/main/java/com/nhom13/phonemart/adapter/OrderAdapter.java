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
import com.nhom13.phonemart.dto.ImageDto;
import com.nhom13.phonemart.dto.OrderDto;
import com.nhom13.phonemart.dto.OrderItemDto;
import com.nhom13.phonemart.model.interfaces.OnOrderActionListener;
import com.nhom13.phonemart.util.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<OrderDto> orderDtos;
    private OnOrderActionListener listener;

    public OrderAdapter(Context context, List<OrderDto> orderDtos, OnOrderActionListener listener) {
        this.context = context;
        this.orderDtos = orderDtos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderAdapter.OrderViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {
        OrderDto orderDto = orderDtos.get(position);

        holder.textView_allOrder_branchName.setText(orderDto.getBranch().getName());
        holder.textView_allOrder_status.setText(String.valueOf(orderDto.getOrderStatus()));
        holder.textView_allOrder_number.setText(String.valueOf(orderDto.getId()));
        holder.textView_allOrder_date.setText(String.valueOf(orderDto.convertDate()));

        List<OrderItemDto> orderItemDtos = new ArrayList<>(orderDto.getOrderItems());
        int quantity = orderItemDtos.stream()
                .mapToInt(OrderItemDto::getQuantity)
                .sum();

        holder.textView_allOrder_quantity.setText(String.valueOf(quantity));
        holder.textView_allOrder_price.setText(String.format("$ %s", orderDto.getTotalAmount()));

        List<ImageDto> imageDtos = orderItemDtos.get(0).getProduct().getImages();
        if (imageDtos != null && !imageDtos.isEmpty()) {
            ImageUtils.loadImageIntoImageView(context, imageDtos.get(0).getId(), holder.imageView_allOrder_image);
        } else {
            Glide.with(context)
                    .load(R.drawable.delivery_truck_ico)
                    .into(holder.imageView_allOrder_image);
        }
    }

    @Override
    public int getItemCount() {
        return orderDtos.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private OnOrderActionListener listener;

        private ImageView imageView_allOrder_image;
        private TextView textView_allOrder_branchName, textView_allOrder_status, textView_allOrder_number, textView_allOrder_date, textView_allOrder_quantity, textView_allOrder_price;

        public OrderViewHolder(@NonNull View itemView, OnOrderActionListener listener) {
            super(itemView);

            OrderViewHolder.this.listener = listener;

            imageView_allOrder_image = itemView.findViewById(R.id.imageView_allOrder_image);
            textView_allOrder_branchName = itemView.findViewById(R.id.textView_allOrder_branchName);
            textView_allOrder_status = itemView.findViewById(R.id.textView_allOrder_status);
            textView_allOrder_number = itemView.findViewById(R.id.textView_allOrder_number);
            textView_allOrder_date = itemView.findViewById(R.id.textView_allOrder_date);
            textView_allOrder_quantity = itemView.findViewById(R.id.textView_allOrder_quantity);
            textView_allOrder_price = itemView.findViewById(R.id.textView_allOrder_price);

            itemView.setOnClickListener(v -> listener.onClickOrder(getBindingAdapterPosition()));

        }

    }
}
