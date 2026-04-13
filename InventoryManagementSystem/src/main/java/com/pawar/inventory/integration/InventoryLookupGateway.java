package com.pawar.inventory.integration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawar.inventory.model.Item;
import com.pawar.inventory.model.Location;
import com.pawar.inventory.model.Lpn;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@Dependent
public class InventoryLookupGateway {

	private static final Logger logger = LoggerFactory.getLogger(InventoryLookupGateway.class);

	@Inject
	@ConfigProperty(name = "inventory.lookup.base-url")
	private String baseUrl;

	private final ObjectMapper objectMapper;

	@Inject

	public InventoryLookupGateway(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public Lpn fetchLpn(String lpnName) throws IOException {
		return fetchData("lpns", lpnName, Lpn.class);
	}

	public Location fetchLocation(String locnBrcd) throws IOException {
		return fetchData("locations", locnBrcd, Location.class);
	}

	public Item fetchItem(String itemName) throws IOException {
		return fetchData("items", itemName, Item.class);
	}

	private <T> T fetchData(String serviceName, String value, Class<T> targetType) throws IOException {
		String json = restGetCall(baseUrl + serviceName + "/list/by-name/" + value);
		return objectMapper.readValue(json, targetType);
	}

	private String restGetCall(String urlText) throws IOException {
		logger.info("URL : {}", urlText);
		URL url = new URL(urlText);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		int statusCode = connection.getResponseCode();
		byte[] responseBytes;
		if (statusCode >= 200 && statusCode < 300) {
			responseBytes = connection.getInputStream().readAllBytes();
		} else {
			byte[] errorBytes = connection.getErrorStream() != null
					? connection.getErrorStream().readAllBytes()
					: new byte[0];
			String errorBody = new String(errorBytes, StandardCharsets.UTF_8);
			throw new IOException("Lookup call failed with status " + statusCode + ": " + errorBody);
		}

		return new String(responseBytes, StandardCharsets.UTF_8);
	}
}