package com.team6.apps.search.controllers;

import com.team6.apps.search.model.Product;
import com.team6.apps.search.service.ProductSearchService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
@Api(value="Index Api", description="Operations pertaining indexing of products for Crackdeal")
public class IndexController {

	@Autowired
	private ProductSearchService productSearchService;

	@PostMapping("/product")
	public Boolean indexProducts(@RequestBody Product product) throws Exception {
		return productSearchService.indexProduct(product);

	}
}
