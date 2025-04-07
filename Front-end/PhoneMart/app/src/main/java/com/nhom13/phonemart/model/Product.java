package com.nhom13.phonemart.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String prodName;
    private int prodPrice;
    private String prodImgUrl;


    public Product(String prodName, int prodPrice, String prodImgUrl) {
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.prodImgUrl = prodImgUrl;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public int getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(int prodPrice) {
        this.prodPrice = prodPrice;
    }

    public String getProdImgUrl() {
        return prodImgUrl;
    }

    public void setProdImgUrl(String prodImgUrl) {
        this.prodImgUrl = prodImgUrl;
    }
}
