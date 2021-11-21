package com.stock.finance.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
*/
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockInfoWrapper;
import com.stock.finance.model.api.ApiAppResponse;

public class StockAPIControllerUtilities {
	
	//object mapper to serialize json string 
	static	ObjectMapper jsonMapperObj = new ObjectMapper();
	
	
	Function <StockInfoWrapper,Boolean> validateInputValues = input -> {
		// if the average stock price is 0 but the stock count is greater than 0
		if(input.getAvgStockPrice() == 0.0f && input.getStockCount() >= 0.0f) return false;
		if(input.getAvgStockPrice() >= 0.0f && input.getStockCount() == 0.0f) return false;
		return true;
	};
	

	/**
	 * Below method will return the filter out list of duplicate items
	 * @param input
	 * @return
	 */
	List<StockInfoWrapper> filterDuplicateStockInfo (List<StockInfoWrapper> input){
	   List<String> duplicateSymbols = findDuplicateSymbols(input);
       return input.stream().filter(n -> !duplicateSymbols.contains(n.getSymbol())).collect(Collectors.toList());
	}

	/**
	 * Returns the list of string that are duplicate 
	 * @param input
	 * @return
	 */
	List<String> findDuplicateSymbols(List<StockInfoWrapper> input){
        Set<String> items = new HashSet<>();
		
		// !items.add(n.getSymbol()) - set returns false if the same element is added again
        // Return the set of duplicate elements
        return input.stream().filter(n -> !items.add(n.getSymbol())).map(StockInfoWrapper::getSymbol).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	static ApiAppResponse createResponse(String status, Optional<?> information) {
		ApiAppResponse response = new ApiAppResponse();
		response.setStatus(status);
		if(!information.isEmpty()) {
			Object info = information.get();
			List<StockInfoWrapper> stockInfoWrapperLst = new ArrayList<>();
			if ( info instanceof StockInfo) {
				StockInfo tmpStock = (StockInfo)info;
				//Create StockInfoWrapper object
				StockInfoWrapper stockInfoWrapper = 
						StockInfoWrapper.builder()
										.symbol(tmpStock.getSymbol())
										.avgStockPrice(tmpStock.getAvgStockPrice())
										.stockCount(tmpStock.getStockCount())
										.build();
				
				stockInfoWrapperLst.add(stockInfoWrapper);
				//response.setStockInfo((List<StockInfo>)info);
			}
			if(info instanceof java.util.List) {
				List<StockInfo> stockList = (List<StockInfo>)info;
				stockInfoWrapperLst = stockList.stream()
						.map(item -> new StockInfoWrapper(item.getSymbol(), item.getAvgStockPrice(), item.getStockCount()))
						.collect(Collectors.toList());
			}
			response.setStockInfo(stockInfoWrapperLst);
		}
		return response;
	}
	
	/**
	 * This method will return the duplicate items passed in the input -  
	 * NOT using below method, since the duplicate symbol will be ignored from the list of input
	 * And displayed to the customer as a response.
	 * @param input
	 * @return
	 */
	 List<StockInfoWrapper> findDuplicateFromList(List<StockInfoWrapper> input){
		Set<String> items = new HashSet<>();
		
		// !items.add(n.getSymbol()) - set returns false if the same element is added again
        // Return the set of duplicate elements
        return input.stream().filter(n -> !items.add(n.getSymbol())).collect(Collectors.toList());
	}

	
	/**
	 * Passing the java pojo will be validated against the schema
	 * @param pojoInput
	 * @return
	 */
	/**
	 * Below function is not being used right now, since the Body of the POST request is already converted by Spring
	 * Need to apply different logical validation
	 */
	 /* Add below dependency to enable the json validator
	  	<dependency>
			<groupId>org.everit.json</groupId>
			<artifactId>org.everit.json.schema</artifactId>
			<version>1.3.0</version>
		</dependency>
	  */
	/* 
	Function<Object,Boolean> validateInputJson = pojoInput -> {
		
		String schmeaFile = "input-schema.json"; //if single stock input use specific schema

		//for list of stock validate using different schema
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(schmeaFile)) {
              System.out.println(jsonMapperObj.writeValueAsString(pojoInput));
			  JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
			  org.everit.json.schema.Schema schema = SchemaLoader.load(rawSchema);
			  schema.validate(new JSONObject(jsonMapperObj.writeValueAsString(pojoInput))); // throws a ValidationException if this object is invalid
			}catch(Exception exe) {
				log.error("Exception occured when validating json schema - ", exe);
				return false; 
			}
		//if validation didn't throw any exception it is successfully validated
		return true;
	};
    */	

}