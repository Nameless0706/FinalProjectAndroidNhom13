package com.nhom13.phonemart.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.math.BigDecimal;




public class OrderItemDto implements Parcelable {
	private Long id;
	private int quantity;
	private BigDecimal unitPrice;
	private ProductDto product;

	public OrderItemDto(Long id, int quantity, BigDecimal unitPrice, ProductDto product) {
		this.id = id;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.product = product;
	}

	protected OrderItemDto(Parcel in) {
		if (in.readByte() == 0) {
			id = null;
		} else {
			id = in.readLong();
		}
		quantity = in.readInt();
	}

	public static final Creator<OrderItemDto> CREATOR = new Creator<OrderItemDto>() {
		@Override
		public OrderItemDto createFromParcel(Parcel in) {
			return new OrderItemDto(in);
		}

		@Override
		public OrderItemDto[] newArray(int size) {
			return new OrderItemDto[size];
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
