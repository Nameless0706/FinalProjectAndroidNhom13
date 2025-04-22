package com.nhom13.phonemart.dto;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.List;



public class ProductDto implements Parcelable {
	private Long id;
	private String name;
	private String brand;
	private BigDecimal price;
	private int inventory;
	private String description;
	private CategoryDto category;
	private List<ImageDto> images;

	public ProductDto(Long id, String name, String brand, BigDecimal price, int inventory, String description, CategoryDto category, List<ImageDto> images) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.price = price;
		this.inventory = inventory;
		this.description = description;
		this.category = category;
		this.images = images;
	}

	protected ProductDto(Parcel in) {
		if (in.readByte() == 0) {
			id = null;
		} else {
			id = in.readLong();
		}
		name = in.readString();
		brand = in.readString();
		inventory = in.readInt();
		description = in.readString();
		images = in.createTypedArrayList(ImageDto.CREATOR);
	}

	public static final Creator<ProductDto> CREATOR = new Creator<ProductDto>() {
		@Override
		public ProductDto createFromParcel(Parcel in) {
			return new ProductDto(in);
		}

		@Override
		public ProductDto[] newArray(int size) {
			return new ProductDto[size];
		}
	};

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(@NonNull Parcel parcel, int i) {
		if (id == null) {
			parcel.writeByte((byte) 0);
		} else {
			parcel.writeByte((byte) 1);
			parcel.writeLong(id);
		}
		parcel.writeString(name);
		parcel.writeString(brand);
		parcel.writeInt(inventory);
		parcel.writeString(description);
		parcel.writeTypedList(images);
	}
}
