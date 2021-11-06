package com.stock.finance.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.finance.model.api.About;

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
	@GetMapping("/info")
	public ResponseEntity<JsonNode> apiInfo() throws JsonMappingException, JsonProcessingException{
		String appInfo = "{\"info\":\"Stock API info message\"}";
		JsonNode jsonResponse = jsonMapper.readTree(appInfo);
		return ResponseEntity.ok(jsonResponse);
	}

	/**
	 * Simple API details method to list the API/App info, version, etc.
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/about")
	public ResponseEntity<About> aboutAPI() throws Exception{
		About about = new About(appName,version,description);
		return ResponseEntity.ok(about);
	}


}
