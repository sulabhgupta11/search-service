package com.team6.apps.search.service;

import com.team6.apps.search.model.Product;

import java.util.List;

public interface ProductIndexService {
	Boolean indexProduct(Product product);
	Boolean indexProducts(List<Product> products);
	Boolean removeProduct(String id);
	Boolean removeProducts(List<Product> products);
	Boolean removeIndex(String index);
}
