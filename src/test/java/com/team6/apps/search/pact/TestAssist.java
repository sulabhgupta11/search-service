package com.team6.apps.search.pact;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;


public class TestAssist {


	private static final Logger LOG = LoggerFactory.getLogger(TestAssist.class);

	/**
	 * Common HTTP header for json.
	 */
	public static final Map<String, String> HEADERS =
			ImmutableMap.<String, String>builder().
					put("Content-Type", "application/json;charset=UTF-8").
					build();

	private TestAssist() {
		// Utility class
	}

	/**
	 * Obtain the resource that is the expected response.
	 */
	@Nullable
	public static String getBody(String bodyReference) {
		try {
			return Resources.toString(Resources.getResource(bodyReference), Charsets.UTF_8);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

		return null;
	}
}

