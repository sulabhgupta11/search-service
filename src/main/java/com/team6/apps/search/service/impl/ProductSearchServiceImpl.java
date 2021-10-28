package com.team6.apps.search.service.impl;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.apps.search.constants.Constants;
import com.team6.apps.search.http.AWSSigningInterceptor;
import com.team6.apps.search.model.Product;
import com.team6.apps.search.model.ProductSearchParameter;
import com.team6.apps.search.service.ProductSearchService;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductSearchServiceImpl implements ProductSearchService {

	@Autowired
	private ObjectMapper objectMapper;

	@Qualifier("productSearchClient")
	@Autowired
	private RestHighLevelClient client;

	@Value("${es.product.domain.endpoint}")
	private String productDomainEndpoint;

	private static String type = "_doc";


	@Override
	public Boolean indexProduct(Product product) {
		try {
			final String document = objectMapper.writeValueAsString(product);
			IndexRequest request = new IndexRequest(Constants.PRODUCT_INDEX, type, product.getId()).source(document, XContentType.JSON);
			final IndexResponse response = client.index(request, RequestOptions.DEFAULT);
			return response != null && response.status().equals(RestStatus.OK);
		} catch (final Exception e) {
//			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public Boolean indexProducts(List<Product> products) {
		return null;
	}

	@Override
	public Boolean removeProduct(Product product) {
		try {
			DeleteRequest deleteRequest = new DeleteRequest(Constants.PRODUCT_INDEX, type, product.getId());
			final DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
			return response != null && response.status().equals(RestStatus.OK);
		} catch (final Exception e) {
//			LOG.error(e.getMessage(), e);
			return false;
		}
	}


	@Override
	public List<Product> findProducts(ProductSearchParameter searchParameter) throws Exception {
		String queryText = searchParameter.getSearchText();
		DisMaxQueryBuilder queryBuilder = QueryBuilders.disMaxQuery().add(QueryBuilders.wildcardQuery("name", queryText)).add(QueryBuilders.wildcardQuery("color", queryText));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(queryBuilder);
		searchSourceBuilder.size(searchParameter.getSize());

		SearchRequest searchRequest = new SearchRequest(Constants.PRODUCT_INDEX);
		searchRequest.source(searchSourceBuilder);

		SearchResponse res =
				client.search(searchRequest, RequestOptions.DEFAULT);

		return getSearchResult(res);
	}

	@Override
	public List<Product> findAllProducts() throws Exception {
		SearchRequest searchRequest = new SearchRequest(Constants.PRODUCT_INDEX);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse =
				client.search(searchRequest, RequestOptions.DEFAULT);

		return getSearchResult(searchResponse);
	}

	private List<Product> getSearchResult(SearchResponse response) {

		SearchHit[] searchHit = response.getHits().getHits();
		List<Product> products = new ArrayList<>();

		if (searchHit.length > 0) {
			Arrays.stream(searchHit)
					.forEach(hit -> products
							.add(objectMapper
									.convertValue(hit.getSourceAsMap(),
											Product.class))
					);
		}
		return products;
	}

}
