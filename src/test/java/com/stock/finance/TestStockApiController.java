package com.stock.finance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;

import com.stock.finance.controller.StockAPIController;
import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockInfoWrapper;
import com.stock.finance.model.api.ApiAppResponse;


public class TestStockApiController {
	
	@Test
	public void TestApiResponse() {
		//StockAPIController stockApi = mock(StockAPIController.class);
		StockAPIController stockApi = new StockAPIController();
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
}
