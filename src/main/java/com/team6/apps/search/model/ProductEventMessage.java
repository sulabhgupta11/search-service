package com.team6.apps.search.model;

public class ProductEventMessage {

	private String eventType;
	private Product product;

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public String toString() {
		return "ProductEventMessage [eventType=" + eventType + ", product=" + product + "]";
	}
}