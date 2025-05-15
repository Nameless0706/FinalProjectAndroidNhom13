package com.nhom13.phonemart.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



public class ProductDto implements Serializable {
	private Long id;
	private String name;
	private String brand;
	private BigDecimal price;
	private int inventory;

	private int soldCount;
	private String description;

	private String dateAdded;
	private CategoryDto category;
	private List<ImageDto> images;

	public ProductDto(Long id, String name, String brand, BigDecimal price, int inventory, int soldCount, String description, CategoryDto category, List<ImageDto> images) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.price = price;
		this.inventory = inventory;
		this.soldCount = soldCount;
		this.description = description;
		this.category = category;
		this.images = images;
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

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public CategoryDto getCategory() {
		return category;
	}

	public void setCategory(CategoryDto category) {
		this.category = category;
	}

	public List<ImageDto> getImages() {
		return images;
	}

	public void setImages(List<ImageDto> images) {
		this.images = images;
	}

	public String getDateAdded() {return dateAdded;}

	public void setDateAdded(String dateAdded) {this.dateAdded = dateAdded;}

	public int getSoldCount() {return soldCount;}

	public void setSoldCount(int soldCount) {this.soldCount = soldCount;}
}
