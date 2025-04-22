package com.nhom13.phonemart.model;

import com.nhom13.phonemart.enums.OwnerType;

import java.sql.Blob;

public class Image {
    private Long id;
    private String name;
    private String type;

    private Blob image;
    private String downloadUrl;

    private OwnerType ownerType;



}
