package com.nhom13.phonemart.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Cart {
    private Long id;
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private Set<CartItem> cartItems = new HashSet<>();
    private User user;


    public Cart(Long id, BigDecimal totalAmount, Set<CartItem> cartItems, User user) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.cartItems = cartItems;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Set<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Set<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
