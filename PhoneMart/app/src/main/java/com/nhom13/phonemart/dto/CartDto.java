package com.nhom13.phonemart.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.Set;


public class CartDto implements Parcelable {
	private Long id;
	private BigDecimal totalAmount;
	private Set<CartItemDto> cartItems;

	protected CartDto(Parcel in) {
		if (in.readByte() == 0) {
			id = null;
		} else {
			id = in.readLong();
		}
	}

	public static final Creator<CartDto> CREATOR = new Creator<CartDto>() {
		@Override
		public CartDto createFromParcel(Parcel in) {
			return new CartDto(in);
		}

		@Override
		public CartDto[] newArray(int size) {
			return new CartDto[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel parcel, int i) {
		if (id == null) {
			parcel.writeByte((byte) 0);
		} else {
			parcel.writeByte((byte) 1);
			parcel.writeLong(id);
		}
	}

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
