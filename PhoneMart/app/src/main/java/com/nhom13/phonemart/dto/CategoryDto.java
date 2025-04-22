package com.nhom13.phonemart.dto;

public class CategoryDto{
	private Long id;
	private String name;
	private ImageDto image;

	public CategoryDto(Long id, String name, ImageDto image) {
		this.id = id;
		this.name = name;
		this.image = image;
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

	public ImageDto getImage() {
		return image;
	}

	public void setImage(ImageDto image) {
		this.image = image;
	}
}
