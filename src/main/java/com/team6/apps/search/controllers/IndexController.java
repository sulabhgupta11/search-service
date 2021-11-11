package com.team6.apps.search.controllers;

import com.team6.apps.search.model.Product;
import com.team6.apps.search.service.ProductSearchService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/index")
@Api(value = "Index Api", description = "Operations pertaining indexing of products for Crackdeal")
public class IndexController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ProductSearchService productSearchService;

	@PostMapping("/product")
	public Boolean indexProducts(@RequestBody Product product) throws Exception {
		logger.info("inside /index/product");
		return productSearchService.indexProduct(product);

	}

	@PostMapping("/products")
	public Boolean indexProducts(@RequestBody List<Product> products) throws Exception {
		logger.info("inside /index/products");
		return productSearchService.indexProducts(products);

	}

	@PostMapping("/removeIndex")
	public Boolean indexProducts(@RequestParam String index) throws Exception {
		logger.info("inside /index/removeIndex");
		return productSearchService.removeIndex(index);

	}
}
