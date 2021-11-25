package com.stock.finance.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


import com.stock.finance.model.api.About;

/*
 * Java 11 http client apporach to perform integration test
 */
public class Java11HttpClient {
	
	@Test
	@Disabled
	public void getEntitiesFromAPI() {
		
		//Create http client 
		HttpClient client = HttpClient.newHttpClient();
		
		// Create request - builder pattern
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://my-stock-boot-app.herokuapp.com/stock-app/about"))
				     .build();
		// Send request and receive response
		//Convert the repsonse as a string, which can also be a About object - use BodyHandlers to convert
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			assertEquals(200, response.statusCode());
			assertEquals(true, response.body()!= null && response.body().contains("Stock"));
		} catch (IOException | InterruptedException e ) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
	
	public void postEntitiesFromAPI() {
		
		//Create http client 
		HttpClient client = HttpClient.newHttpClient();
		
		// Create request - builder pattern
		HttpRequest request = HttpRequest.newBuilder()
				.header("Content-Type", "application/json")
				.uri(URI.create("https://my-stock-boot-app.herokuapp.com/stock-app/about")) //upper case
				.POST(BodyPublishers.ofString("{\"username\":\"user\",\"password\":\"password\"}"))
				.build();
		// Send request and receive response
		//Convert the repsonse as a string, which can also be a About object - use BodyHandlers to convert
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			assertEquals(200, response.statusCode());
			assertEquals(true, response.body()!= null && response.body().contains("Stock"));
		} catch (IOException | InterruptedException e ) {
			e.printStackTrace();
			assertTrue(false);
		}
	}


}
