package com.nhom13.phonemart.dto;

import java.io.Serializable;
import java.util.Set;

public class UserDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private ImageDto image;
    private CartDto cart;
    private Set<OrderDto> orders;
    private Set<ProductDto> favoriteProducts;

    public UserDto(Long id, String firstName, String lastName, String email, ImageDto image, CartDto cart, Set<OrderDto> orders, Set<ProductDto> favoriteProducts) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.image = image;
        this.cart = cart;
        this.orders = orders;
        this.favoriteProducts = favoriteProducts;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public CartDto getCart() {
        return cart;
    }

    public void setCart(CartDto cart) {
        this.cart = cart;
    }

    public Set<OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(Set<OrderDto> orders) {
        this.orders = orders;
    }

    public Set<ProductDto> getFavoriteProducts() {
        return favoriteProducts;
    }
}
