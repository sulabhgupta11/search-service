package com.team6.apps.search.model;

import io.swagger.annotations.ApiModelProperty;

public class Product {

	@ApiModelProperty(value="vivo-1", example="vivo-1")
	private String id;

	private String productId;
	
	@ApiModelProperty(value="vivo", example="vivo")
	private String name;
	
	@ApiModelProperty(value="X60",example="X60")
	private String description;
	
	@ApiModelProperty(value="White", example="White")
	private String color;
	
	@ApiModelProperty(value="12000", example="12000")
	private String price;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		this.productId = id;
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

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
		this.id = productId;
	}
}
