package com.nhom13.phonemart.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.nhom13.phonemart.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;



public class OrderDto implements Parcelable {
	private Long id;
	private LocalDate orderDate;
	private OrderStatus orderStatus;
	private BigDecimal totalAmount;
	private Set<OrderItemDto> orderItems;

	public OrderDto(Long id, LocalDate orderDate, OrderStatus orderStatus, BigDecimal totalAmount, Set<OrderItemDto> orderItems) {
		this.id = id;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.totalAmount = totalAmount;
		this.orderItems = orderItems;
	}

	protected OrderDto(Parcel in) {
		if (in.readByte() == 0) {
			id = null;
		} else {
			id = in.readLong();
		}
	}

	public static final Creator<OrderDto> CREATOR = new Creator<OrderDto>() {
		@Override
		public OrderDto createFromParcel(Parcel in) {
			return new OrderDto(in);
		}

		@Override
		public OrderDto[] newArray(int size) {
			return new OrderDto[size];
		}
	};

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDate orderDate) {
		this.orderDate = orderDate;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Set<OrderItemDto> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Set<OrderItemDto> orderItems) {
		this.orderItems = orderItems;
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
	}
}
