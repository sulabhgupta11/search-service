package com.team6.apps.search.controllers;

import com.team6.apps.search.model.Product;
import com.team6.apps.search.service.ProductIndexService;
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
	private ProductIndexService productIndexService;

	@PostMapping("/addProduct")
	public Boolean indexProduct(@RequestBody Product product) throws Exception {
		logger.info("inside /index/addProduct");
		return productIndexService.indexProduct(product);

	}

	@PostMapping("/addProducts")
	public Boolean indexProducts(@RequestBody List<Product> products) throws Exception {
		logger.info("inside /index/addProducts");
		return productIndexService.indexProducts(products);

	}

	@PostMapping("/removeProduct")
	public Boolean removeProduct(@RequestParam String productId) throws Exception {
		logger.info("inside /index/removeProduct");
		return productIndexService.removeProduct(productId);

	}

	@PostMapping("/removeIndex")
	public Boolean remoceIndex(@RequestParam String index) throws Exception {
		logger.info("inside /index/removeIndex");
		return productIndexService.removeIndex(index);

	}
}
