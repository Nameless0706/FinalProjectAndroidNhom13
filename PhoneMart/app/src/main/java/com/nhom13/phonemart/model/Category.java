package com.nhom13.phonemart.model;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {

    private Long id;

    private String name;

    private Image image;

    private List<Product> products;


    public Category(Long id, String name, Image image, List<Product> products) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.products = products;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
