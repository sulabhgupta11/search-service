package com.team6.apps.search.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.apps.search.model.Product;
import com.team6.apps.search.service.ProductIndexService;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProductIndexServiceImpl implements ProductIndexService {

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
	public Boolean removeProduct(String id) {
		try {
			DeleteRequest deleteRequest = new DeleteRequest(productIndex, type, id);
			final DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
			return response != null && response.status().equals(RestStatus.OK);
		} catch (final Exception e) {
//			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public Boolean removeProducts(List<Product> products) {
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

}
