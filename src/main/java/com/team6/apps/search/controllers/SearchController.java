package com.team6.apps.search.controllers;


import com.team6.apps.search.model.Product;
import com.team6.apps.search.model.ProductSearchParameter;
import com.team6.apps.search.service.ProductSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/search")
@Api(value="Search App", description="Operations pertaining search for Crackdeal")
public class SearchController {

	@Autowired
	private ProductSearchService productSearchService;

	@ApiOperation(value = "Search for all product",response = List.class)
	@GetMapping("/getAllProducts")
	public List<Product> getAllProducts() throws Exception {
		List<Product> list = productSearchService.findAllProducts();
		return list;

	}

	@ApiOperation(value = "Search for the product",response = List.class)
	@PostMapping("/getProducts")
	public List<Product> getProducts(ProductSearchParameter searchParameter)throws Exception {
		List<Product> list = productSearchService.findProducts(searchParameter);
		return list;

	}



}
