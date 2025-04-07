package com.nhom13.phonemart.model;

import java.io.Serializable;

public class Category implements Serializable {
    private String cateName;

    private String cateImgUrl;

    public Category(String cateName, String cateImgUrl) {
        this.cateName = cateName;
        this.cateImgUrl = cateImgUrl;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public String getCatImgUrl() {
        return cateImgUrl;
    }

    public void setCatImgUrl(String cateImgUrl) {
        this.cateImgUrl = cateImgUrl;
    }
}
