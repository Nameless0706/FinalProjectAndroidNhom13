package com.nhom13.phonemart.model;

import com.nhom13.phonemart.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Order {
    private Long id;

    private LocalDate orderDate;

    private OrderStatus orderStatus;

    private BigDecimal totalAmount;

    public Order(Long id, LocalDate orderDate, OrderStatus orderStatus, BigDecimal totalAmount) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
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
}
