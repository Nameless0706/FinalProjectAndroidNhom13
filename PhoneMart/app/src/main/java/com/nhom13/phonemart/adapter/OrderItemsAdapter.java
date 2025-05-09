package com.nhom13.phonemart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom13.phonemart.R;
import com.nhom13.phonemart.dto.OrderItemDto;
import com.nhom13.phonemart.model.interfaces.OnOrderItemActionListener;

import java.math.BigDecimal;
import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.OrderItemsViewHolder> {
    private Context context;
    private List<OrderItemDto> orderItemDtos;
    private OnOrderItemActionListener listener;

    public OrderItemsAdapter(Context context, List<OrderItemDto> orderItemDtos, OnOrderItemActionListener listener) {
        this.context = context;
        this.orderItemDtos = orderItemDtos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderItemsAdapter.OrderItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_item, parent, false);
        return new OrderItemsAdapter.OrderItemsViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemsAdapter.OrderItemsViewHolder holder, int position) {
        OrderItemDto orderItemDto = orderItemDtos.get(position);

        holder.textView_order_productName.setText(orderItemDto.getProduct().getName());
        holder.textView_order_productPrice.setText(String.format("$ %s", orderItemDto.getUnitPrice()));
        holder.textView_order_productQuantity.setText(String.valueOf(orderItemDto.getQuantity()));
        holder.textView_order_productTotalPrice.setText(String.format("$ %s", orderItemDto.getUnitPrice().multiply(BigDecimal.valueOf(orderItemDto.getQuantity()))));

    }

    @Override
    public int getItemCount() {
        return orderItemDtos.size();
    }

    public static class OrderItemsViewHolder extends RecyclerView.ViewHolder {
        OnOrderItemActionListener listener;
        TextView textView_order_productName, textView_order_productPrice, textView_order_productQuantity, textView_order_productTotalPrice;

        public OrderItemsViewHolder(@NonNull View itemView, OnOrderItemActionListener listener) {
            super(itemView);

            textView_order_productName = itemView.findViewById(R.id.textView_order_productName);
            textView_order_productPrice = itemView.findViewById(R.id.textView_order_productPrice);
            textView_order_productQuantity = itemView.findViewById(R.id.textView_order_productQuantity);
            textView_order_productTotalPrice = itemView.findViewById(R.id.textView_order_productTotalPrice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickOrderItem(getBindingAdapterPosition());
                }
            });

        }

    }
}
