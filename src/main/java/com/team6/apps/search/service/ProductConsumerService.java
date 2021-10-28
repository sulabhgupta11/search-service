package com.team6.apps.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team6.apps.search.constants.Constants;
import com.team6.apps.search.model.ProductEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class ProductConsumerService {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductSearchService productSearchService;


	@KafkaListener(topics = Constants.PRODUCT_TOPIC_NAME, groupId = Constants.PRODUCT_CONSUMER_GROUP)
	public void consume(String message) {
//		logger.info(String.format("Message recieved -> %s", message));
		try {
			ProductEventMessage eventMessage = objectMapper.readValue(message, ProductEventMessage.class);
			if (eventMessage.getEventType().equals(Constants.PRODUCT_ADDED_EVENT) || eventMessage.getEventType().equals(Constants.PRODUCT_UPDATED_EVENT)) {
				productSearchService.indexProduct(eventMessage.getProduct());
			} else if (eventMessage.getEventType().equals(Constants.PRODUCT_REMOVED_EVENT)) {
				productSearchService.removeProduct(eventMessage.getProduct());
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
