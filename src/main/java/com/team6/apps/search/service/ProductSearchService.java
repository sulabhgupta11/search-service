package com.team6.apps.search.service;

import com.team6.apps.search.model.Product;
import com.team6.apps.search.model.ProductSearchParameter;

import java.util.List;

public interface ProductSearchService {

	Boolean indexProduct(Product product);
	Boolean indexProducts(List<Product> products);
	Boolean removeProduct(Product product);
	List<Product> findAllProducts() throws Exception;
	List<Product> findProducts(ProductSearchParameter searchParameter) throws Exception;

}
