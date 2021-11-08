package com.team6.apps.search.controllers;


import com.team6.apps.search.model.Product;
import com.team6.apps.search.model.ProductSearchParameter;
import com.team6.apps.search.service.ProductSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Api(value="Search App", description="Operations pertaining search for Crackdeal")
public class SearchController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ProductSearchService productSearchService;

	@ApiOperation(value = "Search for all product",response = List.class)
	@GetMapping("/getAllProducts")
	public List<Product> getAllProducts() throws Exception {
		logger.info("inside /search/getAllProducts");
		List<Product> list = productSearchService.findAllProducts();
		return list;

	}

	@ApiOperation(value = "Search for the product",response = List.class)
	@PostMapping("/getProducts")
	public List<Product> getProducts(@RequestBody ProductSearchParameter searchParameter)throws Exception {
		logger.info("inside /search/getProducts");
		List<Product> list = productSearchService.findProducts(searchParameter);
		return list;

	}

}
