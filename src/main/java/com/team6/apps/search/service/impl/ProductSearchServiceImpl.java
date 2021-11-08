package com.team6.apps.search.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.apps.search.constants.Constants;
import com.team6.apps.search.model.Product;
import com.team6.apps.search.model.ProductSearchParameter;
import com.team6.apps.search.service.ProductSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

	private static final String type = "_doc";
	private static final int BATCH = 5000;


	@Override
	public Boolean indexProduct(Product product) {
		try {
			final String document = objectMapper.writeValueAsString(product);
			IndexRequest request = new IndexRequest(productIndex, type, product.getId()).source(document, XContentType.JSON);
			final IndexResponse response = client.index(request, RequestOptions.DEFAULT);
			return response != null && response.status().equals(RestStatus.OK);
		} catch (final Exception e) {
//			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public Boolean indexProducts(List<Product> products) {
		try {
			BulkRequest bulkRequest = new BulkRequest();
			int count = 0;
			for (Product product : products) {
				final String document = objectMapper.writeValueAsString(product);
				bulkRequest.add(new IndexRequest(productIndex, type, product.getId()).source(document, XContentType.JSON));
				count++;
				if (count % BATCH == 0) {
					sendBulkRequest(bulkRequest);
					count = 0;
					bulkRequest = new BulkRequest();
				}
			}
			if (bulkRequest.numberOfActions() > 0) {
				sendBulkRequest(bulkRequest);
			}
			return true;
		} catch (final Exception e) {
			//			LOG.error(e.getMessage(), e);
			return false;
		}
	}


	private void sendBulkRequest(BulkRequest request) throws IOException {
		BulkResponse bulkresp = client.bulk(request, RequestOptions.DEFAULT);
		if (bulkresp.hasFailures()) {
			for (BulkItemResponse bulkItemResponse : bulkresp) {
				if (bulkItemResponse.isFailed()) {
					BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
					System.out.println("Error " + failure.toString());
				}
			}
		}
	}

	@Override
	public Boolean removeIndex(String index) {
		try {
			DeleteIndexRequest req = new DeleteIndexRequest(index);
			AcknowledgedResponse res = client.indices().delete(req, RequestOptions.DEFAULT);
			return res != null && res.isAcknowledged();
		} catch (final Exception e) {
//			LOG.error(e.getMessage(), e);
			return false;
		}
	}


	@Override
	public Boolean removeProduct(Product product) {
		try {
			DeleteRequest deleteRequest = new DeleteRequest(productIndex, type, product.getId());
			final DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
			return response != null && response.status().equals(RestStatus.OK);
		} catch (final Exception e) {
//			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public Boolean removeProducts(List<Product> products){
		try {
			BulkRequest bulkRequest = new BulkRequest();
			int count = 0;
			for (Product product : products) {
				final String document = objectMapper.writeValueAsString(product);
				bulkRequest.add(new DeleteRequest(productIndex, type, product.getId()));
				count++;
				if (count % BATCH == 0) {
					sendBulkRequest(bulkRequest);
					count = 0;
					bulkRequest = new BulkRequest();
				}
			}
			if (bulkRequest.numberOfActions() > 0) {
				sendBulkRequest(bulkRequest);
			}
			return true;
		} catch (final Exception e) {
			//			LOG.error(e.getMessage(), e);
			return false;
		}
	}


	@Override
	public List<Product> findProducts(ProductSearchParameter searchParameter) throws Exception {
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
