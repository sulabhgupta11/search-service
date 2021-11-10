package com.team6.apps.search.model;

import io.swagger.annotations.ApiModelProperty;

public class Product {

	@ApiModelProperty(value = "vivo-1", example = "vivo-1")
	private String id;

	@ApiModelProperty(value = "vivo", example = "vivo")
	private String name;

	@ApiModelProperty(value = "X60", example = "X60")
	private String description;

	@ApiModelProperty(value = "White", example = "White")
	private String color;

	@ApiModelProperty(value = "12000", example = "12000")
	private String price;

	@ApiModelProperty(value = "Image url of the product", example = "s3://crackdeal/products/images/Iphone13-pro-1.jpg")
	private String imageUrl;

	@ApiModelProperty(value = "brand", example = "Apple")
	private String brand;

	@ApiModelProperty(value = "category", example = "Mobiles")
	private String category;

	@ApiModelProperty(value = "stock", example = "300")
	private int stock;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
