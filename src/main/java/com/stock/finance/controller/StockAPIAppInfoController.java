package com.stock.finance.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.finance.model.api.About;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/stock-app/")
public class StockAPIAppInfoController {
	
	
	@Value("${stockapp.description}")
	private String description;

	@Value("${stockapp.version}")
	private String version;

	@Value("${stockapp.name}")
	private String appName;

	//object mapper to serialize json string 
	static	ObjectMapper jsonMapper = new ObjectMapper();

	/**
	 * Simple method used for printing simple text message used for validating purpose
	 * @return
	 * @throws JsonMappingException
	 * @throws JsonProcessingException
	 */
	@Operation(description = "This end-point will display information about the application. used mostly for testing.")
	@GetMapping("/info")
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	public ResponseEntity<JsonNode> apiInfo() throws JsonMappingException, JsonProcessingException{
		String appInfo = "{\"info\":\"Stock API info message\"}";
		String appContent = getSampleInfo();
		JsonNode jsonResponse = jsonMapper.readTree(appContent);
		return ResponseEntity.ok(jsonResponse);
	}

	/**
	 * Simple API details method to list the API/App info, version, etc.
	 * @return
	 * @throws Exception
	 */
	@Operation(description = "This end-point will list the details about the applicaton and version details.",
			responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = About.class)))})
	@GetMapping(value="/about",produces="application/json")
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	public ResponseEntity<About> aboutAPI() throws Exception{
		About about = new About(appName,version,description);
		return ResponseEntity.ok(about);
	}
	
	/*
	 * Sample output generator for view
	 */
	private static String getSampleInfo() {
		String output = "{"
				+ "    \"stockInfo\": ["
				+ "        {"
				+ "            \"symbol\": \"MSFT\","
				+ "            \"currentPrice\": 300.06,"
				+ "            \"companyName\": \"Microsoft Corporation\","
				+ "            \"lastAccessed\": \"2021-11-07T17:38:31.7269808\","
				+ "            \"currentInvestedAmount\": 200.00,"
				+ "            \"actualInvestedAmount\": 150.002,"
				+ "            \"difference\": 50.00,"
				+ "            \"profitOrLoss\": \"** Profit **\""
				+ "        },"
				+ "        {"
				+ "            \"symbol\": \"INTC\","
				+ "            \"currentPrice\": 50.92,"
				+ "            \"companyName\": \"Intel Corporation\","
				+ "            \"lastAccessed\": \"2021-11-07T17:38:31.8349824\","
				+ "            \"currentInvestedAmount\": 98.84,"
				+ "            \"actualInvestedAmount\": 100.00,"
				+ "            \"difference\": -1.16,"
				+ "            \"profitOrLoss\": \"** Loss **\""
				+ "        },"
				+ "        {"
				+ "            \"symbol\": \"GAIN\","
				+ "            \"currentPrice\": 16.00,"
				+ "            \"companyName\": \"Gladstone Investment\","
				+ "            \"lastAccessed\": \"2021-11-07T17:38:31.8349824\","
				+ "            \"currentInvestedAmount\": 32.00,"
				+ "            \"actualInvestedAmount\": 30.00,"
				+ "            \"difference\": 2.00,"
				+ "            \"profitOrLoss\": \"** Profit **\""
				+ "        },"
				+ "        {"
				+ "            \"symbol\": \"SBUX\","
				+ "            \"currentPrice\": 91.42,"
				+ "            \"companyName\": \"Starbucks\","
				+ "            \"lastAccessed\": \"2021-11-07T17:38:31.8349824\","
				+ "            \"currentInvestedAmount\": 100.111,"
				+ "            \"actualInvestedAmount\": 91.2222,"
				+ "            \"difference\": 8.889,"
				+ "            \"profitOrLoss\": \"** Profit **\""
				+ "        }"
				+ "    ],"
				+ "    \"investedAmount\": 371.224,"
				+ "    \"currentMarketTotalAmount\": 430.951,"
				+ "    \"difference\": 59.727,"
				+ "    \"profitLossStatus\": \"**PROFIT**\","
				+ "    \"lastAccessed\": \"2021-11-07T17:38:31.8589826\","
				+ "    \"simpleStatus\": \"Successfully computed.\""
				+ "}";
		
		return output;
	}
}
