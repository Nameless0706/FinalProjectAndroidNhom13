package com.nhom13.phonemart.model;

import java.util.List;

public class User {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String otp;
    private String password;
    private Boolean isVerified;

    private List<Order> orders;
    private List<Product> favoriteProducts;

    public User(int id, String email, String firstName, String lastName, String otp, String password, Boolean isVerified, List<Order> orders, List<Product> favoriteProducts) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.otp = otp;
        this.password = password;
        this.isVerified = isVerified;
        this.orders = orders;
        this.favoriteProducts = favoriteProducts;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getVerified() {
        return isVerified;
    }

    public void setVerified(Boolean verified) {
        isVerified = verified;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<Product> getFavoriteProducts() {
        return favoriteProducts;
    }
}
