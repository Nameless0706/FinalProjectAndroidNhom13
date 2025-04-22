package com.nhom13.phonemart.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.math.BigDecimal;


public class CartItemDto implements Parcelable {
	private Long id;
	private int quantity;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	private ProductDto product;

	public CartItemDto(Long id, int quantity, BigDecimal unitPrice, BigDecimal totalPrice, ProductDto product) {
		this.id = id;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.totalPrice = totalPrice;
		this.product = product;
	}

	protected CartItemDto(Parcel in) {
		if (in.readByte() == 0) {
			id = null;
		} else {
			id = in.readLong();
		}
		quantity = in.readInt();
	}

	public static final Creator<CartItemDto> CREATOR = new Creator<CartItemDto>() {
		@Override
		public CartItemDto createFromParcel(Parcel in) {
			return new CartItemDto(in);
		}

		@Override
		public CartItemDto[] newArray(int size) {
			return new CartItemDto[size];
		}
	};

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public ProductDto getProduct() {
		return product;
	}

	public void setProduct(ProductDto product) {
		this.product = product;
	}

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
		parcel.writeInt(quantity);
	}
}
