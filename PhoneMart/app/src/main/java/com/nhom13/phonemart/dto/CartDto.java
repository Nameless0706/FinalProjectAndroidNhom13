package com.nhom13.phonemart.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;


public class CartDto implements Serializable {
	private Long id;
	private BigDecimal totalAmount;
	private Set<CartItemDto> cartItems;





	public Long getId() {
		return id;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public Set<CartItemDto> getCartItems() {
		return cartItems;
	}
}
