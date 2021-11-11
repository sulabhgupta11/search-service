package com.team6.apps.search.integration;

import com.team6.apps.search.config.SearchConfig;
import com.team6.apps.search.model.Product;
import com.team6.apps.search.model.ProductSearchParameter;
import com.team6.apps.search.service.ProductIndexService;
import com.team6.apps.search.service.ProductSearchService;
import com.team6.apps.search.service.impl.ProductIndexServiceImpl;
import com.team6.apps.search.service.impl.ProductSearchServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.BDDAssertions.then;

//@ClusterScope(scope = Scope.SUITE)
//@ThreadLeakScope(ThreadLeakScope.Scope.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SearchConfig.class, ProductSearchServiceImpl.class, ProductIndexServiceImpl.class})
public class ProductServiceTest {

	@Autowired
	private ProductSearchService prodSearchService;

	@Autowired
	private ProductIndexService prodIndexService;

	@Value("${product.index}")
	private String productIndex;

	@Before
	public void setUp() throws InterruptedException{
//		System.setProperty("tests.rest.cluster", "localhost:9200");
		System.setProperty("aws.accessKeyId", "AKIA5IICNMZ5XFEVM76Z");
		System.setProperty("aws.secretAccessKey", "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");
		List<Product> products = buildProducts();
		prodIndexService.indexProducts(products);
		TimeUnit.SECONDS.sleep(2);
	}

	@After
	public void tearDown() throws InterruptedException{
//		System.clearProperty("tests.rest.cluster");
		prodIndexService.removeIndex(productIndex);
		TimeUnit.SECONDS.sleep(2);
	}

	@Test
	public void testFindAllProducts() throws Exception {
		//WHEN
		List<Product> searchedProducts = prodSearchService.findAllProducts();

		// THEN
		then(searchedProducts.size() == 2);


	}

	@Test
	public void testFindProduct1() throws Exception {
		//GIVEN
		ProductSearchParameter searchParameter = new ProductSearchParameter();
		searchParameter.setSearchText("Apple");

		//WHEN
		List<Product> searchedProducts = prodSearchService.findProductsByFilter(searchParameter);

		// THEN
		then(searchedProducts.size() == 1);
		then(searchedProducts.get(0).getBrand().equals("Apple"));

	}


	private List<Product> buildProducts() {

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

	//@TestConfiguration
//	public static class PSTestConfiguration {
//
//		@Bean
//		public RestHighLevelClient productSearchClient() {
//			return new RestHighLevelClient(RestClient.builder(HttpHost.create("localhost:9200")));
//		}
//	}

}
