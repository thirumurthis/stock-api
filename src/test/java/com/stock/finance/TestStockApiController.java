package com.stock.finance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

import com.stock.finance.controller.StockAPIControllerUtilities;
import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockInfoWrapper;
import com.stock.finance.model.api.ApiAppResponse;


public class TestStockApiController {
	
	@Test
	public void TestApiResponse() {
		//StockAPIController stockApi = mock(StockAPIController.class);
		StockAPIControllerUtilities stockApi = new StockAPIControllerUtilities();
		StockInfo stock = new StockInfo(1,"MFST",10.0f,10,"1",true);
		Optional<?> info = Optional.of(stock);
		
		//to invoke a private method
		ApiAppResponse output = ReflectionTestUtils.invokeMethod(stockApi, "createResponse", "Success",info);
		
		//ApiResponse output = stockApi.createResponse("Success", info);
		
		Assert.isTrue("Success".equals(output.getStatus()),"Success returned");
		Assert.isTrue(output.getStockInfo().size()==1,"List didn't match");
		List<StockInfo> stockTestList = new ArrayList<>();
		StockInfo stock2 = new StockInfo(2,"AMZN",10.0f,10,"1",true);
		stockTestList.add(stock);
		stockTestList.add(stock2);
		
		//ApiResponse out = stockApi.createResponse("Success", Optional.of(stockTestList));
		//Invoking private method
		ApiAppResponse out = ReflectionTestUtils.invokeMethod(stockApi, "createResponse", "Success",Optional.of(stockTestList));
		Assert.isTrue("Success".equals(out.getStatus()),"Success returned");
		Assert.isTrue(out.getStockInfo().size()==2,"List didn't match");		
		
	}
	
	//@Test
	/*
	public void testJsonListValidation() {
		
		StockInfoWrapper stock1 = StockInfoWrapper.builder().symbol("ONE").avgStockPrice(10.0f).stockCount(5.0f).build();
		
		StockAPIController stockController = new StockAPIController();
		
		boolean output = ReflectionTestUtils.invokeMethod(stockController, "validateInputJson", stock1);
		//boolean output = ReflectionTestUtils.invokeMethod(stockController, "simpleCheck");
		Assert.isTrue(output,"Valid Json only.");
	}
   */
	
	@Test
	public void testDuplicateItemsList() {
        List<StockInfoWrapper> stockInputList = createInput();
        
		StockAPIControllerUtilities stockController = new StockAPIControllerUtilities();
		
		List<StockInfoWrapper> duplicateOutput = ReflectionTestUtils.invokeMethod(stockController, "findDuplicateFromList", stockInputList);
		Assert.isTrue("TWO".equals(duplicateOutput.get(0).getSymbol()),"There should be one duplicate items that is TWO");
	}
	
	@Test
	public void testDuplicateSymbolList() {
        List<StockInfoWrapper> stockInputList = createInput();
        
		StockAPIControllerUtilities stockController = new StockAPIControllerUtilities();
		
		List<String> duplicateOutput = ReflectionTestUtils.invokeMethod(stockController, "findDuplicateSymbols", stockInputList);
		Assert.isTrue("TWO".equals(duplicateOutput.get(0)),"There should be one duplicate items that is TWO");

	}
	
	@Test
	public void testFilterDuplicateList() {
        List<StockInfoWrapper> stockInputList = createInput();
        
		StockAPIControllerUtilities stockController = new StockAPIControllerUtilities();
		
		List<StockInfoWrapper> duplicateOutput = ReflectionTestUtils.invokeMethod(stockController, "filterDuplicateStockInfo", stockInputList);
		List<String> output = duplicateOutput.stream().map(StockInfoWrapper::getSymbol).collect(Collectors.toList());
		Assert.isTrue(!output.contains("TWO"),"There should not be one duplicate items that is TWO");

	}
	

	private List<StockInfoWrapper> createInput() {
		StockInfoWrapper stock1 = StockInfoWrapper.builder().symbol("ONE").avgStockPrice(10.0f).stockCount(5.0f).build();
        StockInfoWrapper stock2 = StockInfoWrapper.builder().symbol("TWO").avgStockPrice(11.0f).stockCount(6.0f).build();
        StockInfoWrapper stock3 = StockInfoWrapper.builder().symbol("TWO").avgStockPrice(10.0f).stockCount(5.0f).build();
        StockInfoWrapper stock4 = StockInfoWrapper.builder().symbol("THREE").avgStockPrice(10.0f).stockCount(5.0f).build();
		
        List<StockInfoWrapper> stockInputList = new ArrayList<>();
        stockInputList.add(stock1);
        stockInputList.add(stock2);
        stockInputList.add(stock3);
        stockInputList.add(stock4);
		return stockInputList;
	}

}
