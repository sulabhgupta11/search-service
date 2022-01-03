package com.team6.apps.search.pact;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactProviderRuleMk2;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.util.HashMap;
import java.util.Map;


//@RunWith(PactRunner.class)
//@Provider("search_provider")
//@PactFolder("pacts")
public class TestSearchGetAllProducts
{


	private static final String EXPECTED_RES_BODY = TestAssist.getBody("products.json");

	@Rule
	public PactProviderRuleMk2 mockProvider
			= new PactProviderRuleMk2("search_provider", "localhost", 8082, this);


	@Pact(consumer = "search_consumer", provider="search_provider")
	public RequestResponsePact createPact(PactDslWithProvider builder) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");

		return builder
				.given("Pact for validating getAllProducts")
				.uponReceiving("GET REQUEST")
				.method("GET")
				.headers(headers)
				.path("/pact")
				.willRespondWith()
				.status(200).body(EXPECTED_RES_BODY)
				.toPact();
	}


//	@TestTarget
//	public final Target target = new HttpTarget("http", "localhost", 8082, "/search/v1");
//
//	private static ConfigurableWebApplicationContext application;
//
//	@BeforeClass
//	public static void start() {
//		application = (ConfigurableWebApplicationContext)
//				SpringApplication.run(MainApplication.class);
//	}
//
//	@State("Pact for validating getAllProducts")
//	public void toGetState() { }



}

