package com.stock.finance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.user.service.UserAccountService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Disabled
public class TestRestAPIControllerWithTestclient {

	@Autowired 
	TestRestTemplate restTemplate;

	@Test
	void getTest() {
		String body  = restTemplate
				.getForObject("/stock-app/about",String.class);
		System.out.println(body);
		//assertThat(body).contains("Stock");
	}

	@Test
	@Disabled
	@WithMockUser(username = "user",password = "user")
	@WithUserDetails()
	void postTest() {
		String apiKey = getSignUPTest();
		String token = getJWTTokenForTest(apiKey);
		Assert.isTrue(token!=null,"[postTest] JWT token shouldn't be null");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		JsonNode tokenVal = null;
		try {
			root = mapper.readTree(token);
			tokenVal = root.path("jwtToken");
			Assert.isTrue(tokenVal.asText()!=null, "Token received successfully");
		} catch (JsonProcessingException e) {
			Assert.isTrue(false,"Exception occurred: "+e.getMessage());
		}
	}

	@Autowired
	UserAccountService userService;
	
	/*
	 * Common class that can be used to get the token info
	 */
	protected String getJWTTokenForTest(String apiKey) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String bodyContent = "{\"userName\":\"user\",\"apiKey\":\""+apiKey+"\"}";

		HttpEntity<String> requestEntity = new HttpEntity<>(bodyContent, headers);
		String token = restTemplate
				.postForObject("/stock-app/token",requestEntity, String.class);
		return token;
	}
	
	protected String getSignUPTest() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String bodyContent = "{\"userName\":\"user\",\"password\":\"user\"}";

		HttpEntity<String> requestEntity = new HttpEntity<>(bodyContent, headers);
		String token = restTemplate
				.postForObject("/stock-app/signup",requestEntity, String.class);
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		JsonNode apiKey = null;
		try {
			root = mapper.readTree(token);
			apiKey = root.path("apiKey");
			Assert.isTrue(apiKey.asText()!=null, "Token received successfully");
		} catch (JsonProcessingException e) {
			Assert.isTrue(false,"Exception occurred: "+e.getMessage());
		}
		
		return apiKey.asText();
	}

	@Test
	@Disabled
	@WithMockUser(username ="user",password="user")
	@WithUserDetails
	void postGetTest() {
		String apiKey = getSignUPTest();
		String token = getJWTTokenForTest(apiKey);
		Assert.isTrue(token!=null,"[postGetTest] JWT token shouldn't be null");

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = null;
		JsonNode tokenVal = null;
		try {
			root = mapper.readTree(token);
			tokenVal = root.path("jwtToken");
			Assert.isTrue(tokenVal.asText()!=null, "Token received successfully");
		} catch (JsonProcessingException e) {
			Assert.isTrue(false,"Exception occurred: "+e.getMessage());
		}
		if(tokenVal != null) {		
			HttpHeaders getHeaders = new HttpHeaders();
			getHeaders.add("User-Agent", "restTemplate" ); // adding user agent to header which can be any value
			getHeaders.setBearerAuth(tokenVal.asText());

			HttpEntity<?> getReqEntity = new HttpEntity<>(null,getHeaders);

			ResponseEntity<?> response = restTemplate.exchange("/stock/v1/get", HttpMethod.GET, getReqEntity, ArrayList.class);
			Assert.isTrue(response!=null && response.getBody()!=null,"Empty Stock List is expected to return");
			List<?> stockList = (List<?>) response.getBody();
			Assert.isTrue(stockList.isEmpty(),"The inital Stock get list is empty");
		}else{
			Assert.isTrue(false,"Failed to retrive the token.");        	
		}

	}



}
