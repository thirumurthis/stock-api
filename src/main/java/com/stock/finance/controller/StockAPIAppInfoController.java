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
	@Operation(description = "This end-point will list the details about the applicaton and version details.",
			responses = {@ApiResponse(content = @Content(schema = @Schema(implementation = About.class)))})
	@GetMapping(value="/about",produces="application/json")
	public ResponseEntity<About> aboutAPI() throws Exception{
		About about = new About(appName,version,description);
		return ResponseEntity.ok(about);
	}
}
