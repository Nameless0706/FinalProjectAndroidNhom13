package com.nhom13.phonemart.dto;

import com.nhom13.phonemart.enums.OrderStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;



public class OrderDto implements Serializable {
	private Long id;
	private Date orderDate;
	private OrderStatus orderStatus;
	private BigDecimal totalAmount;
	private String address;
	private Set<OrderItemDto> orderItems;
	private BranchDto branch;

	private String paymentMethod;

	private String cardType;

	public OrderDto(Long id, Date orderDate, OrderStatus orderStatus, BigDecimal totalAmount, String address, Set<OrderItemDto> orderItems, BranchDto branch, String paymentMethod, String cardType) {
		this.id = id;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.totalAmount = totalAmount;
		this.address = address;
		this.orderItems = orderItems;
		this.branch = branch;
		this.paymentMethod = paymentMethod;
		this.cardType = cardType;
	}


	public String convertDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(orderDate);
	}

	public Long getId() {return id;}

	public void setId(Long id) {this.id = id;}

	public Date getOrderDate() {return orderDate;}

	public void setOrderDate(Date orderDate) {this.orderDate = orderDate;}

	public OrderStatus getOrderStatus() {return orderStatus;}

	public void setOrderStatus(OrderStatus orderStatus) {this.orderStatus = orderStatus;}

	public BigDecimal getTotalAmount() {return totalAmount;}

	public void setTotalAmount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}

	public String getAddress() {return address;}

	public void setAddress(String address) {this.address = address;}

	public Set<OrderItemDto> getOrderItems() {return orderItems;}

	public void setOrderItems(Set<OrderItemDto> orderItems) {this.orderItems = orderItems;}

	public BranchDto getBranch() {return branch;}

	public void setBranch(BranchDto branch) {this.branch = branch;}

	public String getPaymentMethod() {return paymentMethod;}

	public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}

	public String getCardType() {return cardType;}

	public void setCardType(String cardType) {this.cardType = cardType;}
}
