package com.team6.apps.search.model;

import io.swagger.annotations.ApiModelProperty;


public class ProductSearchParameter {

	@ApiModelProperty(value="vivo", example="vivo")
	private String searchText;

	@ApiModelProperty(value="100", example="100")
	private Integer size;


	public ProductSearchParameter(String searchText, int size) {
		this.searchText = searchText;
		this.size = size;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}



}
