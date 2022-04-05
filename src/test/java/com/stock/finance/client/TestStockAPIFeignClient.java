package com.stock.finance.client;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.stock.finance.model.AuthenticationRequest;
import com.stock.finance.model.StockInfoWrapper;
import com.stock.finance.model.TokenRequest;
import com.stock.finance.model.api.About;
import com.stock.finance.model.api.AuthenticationResponse;
import com.stock.finance.model.api.SimpleStatusResponse;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

// the feign interface is already defined in the test package
// this test class will be using the interface to invoke the stock app
//@Disabled
@SpringBootTest
public class TestStockAPIFeignClient {
	
	private static String ENDPOINT_DOMAIN = "https://my-stock-boot-app.herokuapp.com/";
	
	// NOTE: Add the feign-okhttp dependency
	
	 StockAppFeignClient client = null;
	@BeforeEach
	public void setup() {
	
		client = Feign.builder()
                .decoder(new JacksonDecoder())  // this will be used by feign client to de-serialize the response body
                .encoder(new JacksonEncoder()) //this will be responsible for serializing the request
                .target(StockAppFeignClient.class,ENDPOINT_DOMAIN); // pass the interface client as class ref

	}
	@Test
	void getAppInfo() {
		
	    Object info = client.getAppInfo();
	    System.out.println(info.toString());
	    Assertions.assertNotNull(info);
	    Assertions.assertTrue(info.toString().contains("Stock"),"The returned response should have Stock in it");
	}
	
	@Test
	void getAppVersion() {
		
	    About about = client.getAboutApp();
	    Assertions.assertNotNull(about);
	    Assertions.assertTrue(about.getVersion().contentEquals("v1"),"The returned response should have v1 in it");
	}
	
	@Test
	//@Disabled
	// This test case only for the very first user.
	void testSignUpAndGetStock() {
		
		// 1. signup with test account
		// 2. get the API key
		// 3. generate the token - with API key 
		// 4. hit the get passing token in Authorization header
		//1.
		  final String user = "testuser";
		  final String password = "testpass";
		 AuthenticationRequest request = new AuthenticationRequest(user, password);
	     SimpleStatusResponse apiKeyRespsone = client.signUp(request);
	     
	     Assertions.assertNotNull(apiKeyRespsone);
	     // if this value is null, then the user info already exists in db
	     Assertions.assertNotNull(apiKeyRespsone.getApiKey());
	     Assertions.assertTrue(apiKeyRespsone.getStatusMessage().contains(user));
	     
	     //2. Get the api key 
	     String apiKey = apiKeyRespsone.getApiKey();
	     //3 
	     TokenRequest tokenRequest = new TokenRequest(user,apiKey);
	    
	      AuthenticationResponse tokenResponse = client.getToken(tokenRequest);
	      
	      Assertions.assertNotNull(tokenResponse.getApiKey());
	      Assertions.assertNotNull(tokenResponse.getJwtToken());
	      
	      String tokenInfo = tokenResponse.getJwtToken();
	      
	      //4
	      List<StockInfoWrapper> stock = client.getStock(tokenInfo); // expected to be empty
	      Assertions.assertNotNull(stock);
	}
	
	@Test
	void testgetApiKeyAndGetStock() {
		
		// 1. get the API key
		// 2. generate the token - with API key 
		// 3. hit the get passing token in Authorization header
		//1.
		  final String user = "testuser";
		  final String password = "testpass";
		 AuthenticationRequest request = new AuthenticationRequest(user, password);
	     SimpleStatusResponse apiKeyRespsone = client.getAPIKey(request); //Endpint 
	     
	     Assertions.assertNotNull(apiKeyRespsone);
	     // if this value is null, then the user info already exists in db
	     Assertions.assertNotNull(apiKeyRespsone.getApiKey());
	     
	      String apiKey = apiKeyRespsone.getApiKey();
	     //2
	     TokenRequest tokenRequest = new TokenRequest(user,apiKey);
	    
	      AuthenticationResponse tokenResponse = client.getToken(tokenRequest);
	      
	      //Assertions.assertNotNull(tokenResponse.getApiKey());
	      Assertions.assertNotNull(tokenResponse.getJwtToken());
	      
	      String tokenInfo = tokenResponse.getJwtToken();
	      
	      //3
	      List<StockInfoWrapper> stock = client.getStock(tokenInfo); // expected to be empty
	      Assertions.assertNotNull(stock);
	      Assertions.assertTrue(stock.isEmpty());
	}

}
