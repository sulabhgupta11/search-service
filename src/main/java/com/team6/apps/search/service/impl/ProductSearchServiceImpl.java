package com.team6.apps.search.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.apps.search.model.Product;
import com.team6.apps.search.model.ProductSearchParameter;
import com.team6.apps.search.service.ProductSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductSearchServiceImpl implements ProductSearchService {

	@Autowired
	private ObjectMapper objectMapper;

	@Qualifier("productSearchClient")
	@Autowired
	private RestHighLevelClient client;

	@Value("${es.product.domain.endpoint}")
	private String productDomainEndpoint;

	@Value("${product.index}")
	private String productIndex;


	@Override
	public List<Product> findProductsByFilter(ProductSearchParameter searchParameter) throws Exception {
		String queryText = searchParameter.getSearchText();
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		if (StringUtils.isNotEmpty(queryText)) {
			boolQueryBuilder.must(createFullTextSearchQuery(queryText));
		}

		Map<String, List<ProductSearchParameter.Filter>> byKey = searchParameter.getFilters().stream()
				.collect(Collectors.groupingBy(ProductSearchParameter.Filter::getKey));
		for (Map.Entry<String, List<ProductSearchParameter.Filter>> entry : byKey.entrySet()) {
			BoolQueryBuilder orQueryBuilder = QueryBuilders.boolQuery();
			entry.getValue().forEach(filter -> orQueryBuilder.should(filter.toQuery()));
			boolQueryBuilder.must(orQueryBuilder);
		}
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(boolQueryBuilder);
		searchSourceBuilder.size(searchParameter.getSize());

		SearchRequest searchRequest = new SearchRequest(productIndex);
		searchRequest.source(searchSourceBuilder);

		SearchResponse res =
				client.search(searchRequest, RequestOptions.DEFAULT);

		return getSearchResult(res);
	}

	@Override
	public List<Product> findAllProducts() throws Exception {
		SearchRequest searchRequest = new SearchRequest(productIndex);
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

	private QueryBuilder createFullTextSearchQuery(String searchText) {
		DisMaxQueryBuilder queryBuilder = QueryBuilders.disMaxQuery();
		queryBuilder.add(QueryBuilders.multiMatchQuery(searchText, "name","color", "brand", "category").minimumShouldMatch("40%")
				.fuzziness(Fuzziness.AUTO).type(MultiMatchQueryBuilder.Type.MOST_FIELDS).field("name", 4.0F).field("brand", 3.0F));
		return queryBuilder;
	}

}
