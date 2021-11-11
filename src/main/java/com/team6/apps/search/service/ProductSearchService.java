package com.team6.apps.search.service;

import com.team6.apps.search.model.Product;
import com.team6.apps.search.model.ProductSearchParameter;

import java.util.List;

public interface ProductSearchService {
	List<Product> findAllProducts() throws Exception;
	List<Product> findProductsByFilter(ProductSearchParameter searchParameter) throws Exception;

}
