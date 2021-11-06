package com.stock.finance.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.finance.model.StockInfo;
import com.stock.finance.model.api.About;
import com.stock.finance.model.api.ApiResponse;
import com.stock.finance.service.StockStoreService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/stock/v1")
@Log4j2
//Below is the security configuration, where we include the security schema defined in the main class
//controller class use the securityRequirement schema, to apply security requirement to operation at method level
@SecurityRequirement(name="stockapp-openapi")
public class StockAPIController {


	//object mapper to serialize json string 
	static	ObjectMapper jsonMapper = new ObjectMapper();

	@Autowired
	StockStoreService stockService;
	
	/**
	 * Prints the list of stocks available in database
	 * @return
	 */
	@GetMapping("/get")
	public ResponseEntity<List<StockInfo>> getStockDetails() {
		try {
			List<StockInfo> stockInfo = new ArrayList<>();

			stockInfo = stockService.getStocksDetail();
			// Use if required - for api handling with http status code
			//if(stockInfo.isEmpty()) {
			//	return new ResponseEntity<>(stockInfo,HttpStatus.NO_CONTENT);
			//}else {
     			return new ResponseEntity<>(stockInfo, HttpStatus.OK);
			//}
		}catch(Exception e) {
			log.error("Exception occurred when fetching stock info", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Add a single stock info, when provided as json object
	 * @param stock
	 * @return
	 */
	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addStock(@RequestBody StockInfo stock){
		ApiResponse response;
		try {
			if(stock != null) {
				StockInfo stockInfo = stockService.storeStockInfo(stock);
				response = createResponse("Successfully added stock", Optional.of(stockInfo)); 
				return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
			}else {
				response = createResponse("Stock object is Empty. Cannot add stock.", Optional.of(new ArrayList<StockInfo>())); 
				return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
			}
		}catch(Exception e) {
			log.error("Exception occurred when storing stock info", e);
			response = createResponse("Error adding "+stock.getSymbol()+" "+e.getMessage(), Optional.of(stock));
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Function to add a list of stocks to the database when stocks are provided in 
	 * body of the request as a json list format
	 * @param stock
	 * @return
	 */
	@PostMapping("/add/stocks")
	public ResponseEntity<ApiResponse> addStocks(@RequestBody List<StockInfo> stock){
		ApiResponse response ;
		try {
			List<StockInfo> stockInfo = stockService.storeStocks(stock);
			response = createResponse("Successfully added stocks", Optional.of(stockInfo));
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}catch(Exception e) {
			log.error("Exception occurred when storing stock info", e);
			response = createResponse(e.getMessage(), Optional.of(stock));
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	
	/**
	 * Function to delete the stock by tagging a active flag to false - deactivate for user
	 * @param symbol
	 * @return
	 */
	@DeleteMapping("/delete/{symbol}")
	public ResponseEntity<ApiResponse> deleteStockInfo(@PathVariable("symbol") String symbol){
		ApiResponse response;
		StockInfo stock = null;
		try {
			if(symbol != null) {
				stock = stockService.getStockInfo(symbol);
				//perform soft delete
				if(stock != null && stock.isActive()) {
					stockService.softDeleteStockInfo(symbol,false);
					stock = stockService.getStockInfo(symbol);
					response = createResponse("Successfully deactivated stock", Optional.of(stock));
					return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
				}
				if( stock != null && !stock.isActive()) {
					response = createResponse("Symbol "+symbol+" already deactivated.",Optional.of(stock));
					return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
				}
			}
			response = createResponse("Symbol "+symbol+" NOT Exits.",Optional.of(new ArrayList<StockInfo>()));
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}catch(Exception e) {
			log.error("Exception occurred when deleting stock info using symbol", e);
			response = createResponse("Exception occured deactivating "+symbol+" "+e.getMessage(),Optional.of(new ArrayList<StockInfo>()));
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private final static String FORCE_DELETE = "force";
	
	/**
	 * Function to detete the stock info from data base completely
	 * @param symbol
	 * @param force
	 * @return
	 */
	@DeleteMapping("/delete/{symbol}/{force}")
	public ResponseEntity<ApiResponse> deleteStock(@PathVariable("symbol") String symbol,@PathVariable("force") String force){
		ApiResponse response;
		StockInfo stock = null;
		try {
			boolean forceDelete= false;
			if(symbol != null && force !=null) {
				stock = stockService.getStockInfo(symbol);
				if(stock != null) {
					//perform soft delete
					if(force != null && FORCE_DELETE.equals(force.trim())) {
						forceDelete=true;
					}
					if(forceDelete) {
						//completed remove the symbol from database
						stockService.deleteStockInfo(symbol);	
					}else {
						// deactivate the symbol in database
						stockService.softDeleteStockInfo(symbol,true);
					}
				}
			}else {
				response = createResponse("Symbol "+symbol+" NOT Exits.",Optional.of(new ArrayList<StockInfo>()));
				return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
			}
			if(stock!=null) {
				   response = createResponse("Successfully deleted",Optional.of(stock));
			}else { 
			   response = createResponse("Successfully deleted",Optional.of(new ArrayList<StockInfo>()));
			}
			return new ResponseEntity<ApiResponse>(response,HttpStatus.OK);
		}catch(Exception e) {
			log.error("Exception occurred when deleting stock info using symbol", e);
			response = createResponse("Exception occured deleting "+symbol+" "+e.getMessage(),Optional.of(new ArrayList<StockInfo>()));
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@SuppressWarnings("unchecked")
	private ApiResponse createResponse(String status, Optional<?> information) {
		ApiResponse response = new ApiResponse();
		response.setStatus(status);
		if(!information.isEmpty()) {
			Object info = information.get();
			List<StockInfo> stockInfo = new ArrayList<>();
			if ( info instanceof StockInfo) {
				
				stockInfo.add((StockInfo)info);
				//response.setStockInfo((List<StockInfo>)info);
			}
			if(info instanceof java.util.List) {
				stockInfo = (List<StockInfo>)info;
			}
			response.setStockInfo(stockInfo);
		}
		return response;
	}

}

