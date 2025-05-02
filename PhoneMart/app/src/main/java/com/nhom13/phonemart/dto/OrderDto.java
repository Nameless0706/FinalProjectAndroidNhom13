package com.nhom13.phonemart.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.nhom13.phonemart.enums.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;



public class OrderDto implements Serializable {
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


}
