package com.nhom13.phonemart.model.interfaces;

import com.nhom13.phonemart.dto.CartItemDto;

public interface OnCartItemActionListener {
    void onQuantityChangeRequested(int position, boolean isIncrement);
    void onDeleteCartItem(int position);
    void onClickProductItem(int position);
}

