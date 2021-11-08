package com.team6.apps.search.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.apps.search.config.SearchConfig;
import com.team6.apps.search.controllers.SearchController;
import com.team6.apps.search.model.Product;
import com.team6.apps.search.model.ProductSearchParameter;
import com.team6.apps.search.service.ProductSearchService;
import com.team6.apps.search.service.impl.ProductSearchServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SearchConfig.class, ProductSearchServiceImpl.class})
@WebMvcTest(SearchController.class)
@Import(SearchController.class)
//@ImportAutoConfiguration
//@ComponentScan(basePackages = {"com.team6.apps.search.controllers"})
public class SearchControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProductSearchService searchService;

	@Value("${product.index}")
	private String productIndex;


	@Before
	public void setUp() throws InterruptedException{
		List<Product> products = buildProducts();
		searchService.indexProducts(products);
		TimeUnit.SECONDS.sleep(2);
	}

	@After
	public void tearDown() throws InterruptedException{
         searchService.removeIndex(productIndex);
         TimeUnit.SECONDS.sleep(2);
	}


	@Test
	public void testGetAllProducts() throws Exception {
		System.out.println("Product Index " + productIndex);
		mvc.perform(MockMvcRequestBuilders
				.get("/search/getAllProducts")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(4)));
	}

	@Test
	public void testGetProduct() throws Exception {
		System.out.println("Product Index " + productIndex);
		mvc.perform(MockMvcRequestBuilders
				.post("/search/getProducts")
				.content(asJsonString(buildProductSearchParameter()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}


	private static List<Product> buildProducts() {

		List<Product> products = new ArrayList<>();
		Product product = new Product();
		product.setId("Micromax-1");
		product.setName("Micromax In Note 1");
		product.setColor("Black");
		product.setPrice("11000");
		product.setBrand("Micromax");

		Product product2 = new Product();
		product2.setId("Micromax-2");
		product2.setName("Micromax In A");
		product2.setColor("Black");
		product2.setPrice("8000");
		product2.setBrand("Micromax");

		Product product3 = new Product();
		product3.setId("Micromax-3");
		product3.setName("Micromax In B");
		product3.setColor("Black");
		product3.setPrice("9000");
		product3.setBrand("Micromax");

		Product product4 = new Product();
		product4.setId("Apple-1");
		product4.setName("IPhone 13 pro");
		product4.setColor("Black");
		product4.setPrice("79000");
		product4.setBrand("Apple");

		products.add(product);
		products.add(product2);
		products.add(product3);
		products.add(product4);

		return products;
	}

	private ProductSearchParameter buildProductSearchParameter() {
		ProductSearchParameter searchParameter = new ProductSearchParameter();
		searchParameter.setSearchText("Apple");
		return searchParameter;
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


}
