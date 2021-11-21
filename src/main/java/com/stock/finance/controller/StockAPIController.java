package com.stock.finance.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.stock.finance.model.ComputeStockMetrics;
import com.stock.finance.model.ComputedStockOuputWrapper;
import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockInfoWrapper;
import com.stock.finance.model.StockWrapper;
import com.stock.finance.model.api.ApiAppResponse;
import com.stock.finance.service.ComputeStockMetricsService;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.service.StockStoreService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/stock/v1")
@Log4j2
//Below is the security configuration, where we include the security schema defined in the main class
//controller class use the securityRequirement schema, to apply security requirement to operation at method level
@SecurityRequirement(name="stockapp")
public class StockAPIController extends StockAPIControllerUtilities{


	@Autowired
	StockStoreService stockService;

	@Autowired 
	JWTManagerService jwtService;

	@Autowired
	private UserDetailsService userDetailsService;


	@Autowired
	private ComputeStockMetricsService computeStockMetricsService;

	/**
	 * Prints the list of stocks available in database
	 * @return
	 */
	@Operation(description="This end-point will return the list of Stocks from the database for that user, "
			+ "token is required for access. Use HTTP request header Authorization: Bearer xxx.yyy.zzz",
			responses = { @ApiResponse(content = @Content(array=@ArraySchema(schema=@Schema(implementation= StockInfoWrapper.class))))})
	@GetMapping("/get")
	public ResponseEntity<List<StockInfoWrapper>> getStockInfoFromDataStore(HttpServletRequest request) {
		try {

			//Get the user name based on the token
			String userName = validateTokenAndGetUserName(request);
			if(userName == null) {
				throw new Exception("UserName not exists in database or invalid token provided.");
			}

			List<StockInfoWrapper> stockInfoWrapperList = new ArrayList<>();

			List<StockInfo> stockInfoOutputList= stockService.getStocksDetail(userName);

			stockInfoWrapperList = stockInfoOutputList.stream()
					.map(item -> new StockInfoWrapper(item.getSymbol(), item.getAvgStockPrice(), item.getStockCount()))
					.collect(Collectors.toList());

			// Use if required - for api handling with http status code
			//if(stockInfo.isEmpty()) {
			//	return new ResponseEntity<>(stockInfo,HttpStatus.NO_CONTENT);
			//}else {
			return new ResponseEntity<>(stockInfoWrapperList, HttpStatus.OK);
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
	@Operation(description="This end-point will add specfic stock passed in the POST body, only inserts if valid token is used and returns status response."
			+ "Token is required for access. Use HTTP request header Authorization: Bearer xxx.yyy.zzz",
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= ApiAppResponse.class)))})
	@PostMapping("/add")
	public ResponseEntity<ApiAppResponse> addStock(@RequestBody StockInfoWrapper stock,HttpServletRequest request){
		ApiAppResponse response;
		try {
			// perform json schema validation first on the input
			if (!validateInputValues.apply(stock)) {
				response = createResponse("Input json format NOT vaild.", Optional.of(new ArrayList<StockInfo>())); 
				return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);	
			}
			String userName = validateTokenAndGetUserName(request);
			if(stock != null && stock.getSymbol() != null) {
				//if stock is already available in database don't need to add it again
				StockInfo stockFromDB = stockService.getStockInfoBySymbolAndUser(stock.getSymbol(), userName);
				if(stockFromDB!=null && stockFromDB.getSymbol().equals(stock.getSymbol())) {
					response = createResponse("Stock info already exists in DB.", Optional.of(stockFromDB)); 
					return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);
				}
				//Create stock info object from stockinfo wrapper
				StockInfo inputStockInfo = 
						StockInfo.builder()
						.symbol(stock.getSymbol().toUpperCase()) //convert the symbol to upper case
						.stockCount(stock.getStockCount())
						.avgStockPrice(stock.getAvgStockPrice())
						.userName(userName)
						.build();

				StockInfo stockInfo = stockService.storeStockInfo(inputStockInfo);
				response = createResponse("Successfully added stock", Optional.of(stockInfo)); 
				return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);
			}else {
				response = createResponse("Stock object is Empty. Cannot add stock.", Optional.of(new ArrayList<StockInfo>())); 
				return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);
			}
		}catch(Exception e) {
			log.error("Exception occurred when storing stock info", e);
			response = createResponse("Error adding "+stock.getSymbol()+" "+e.getMessage(), Optional.of(stock));
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Function to add a list of stocks to the database when stocks are provided in 
	 * body of the request as a json list format
	 *  1. validate the token
	 *  2. check if the json is valid, if avgStockPrice = 0.0, then stockcount cannot be >0 and vice versa
	 *  3. check if there are duplicate symbols in the list
	 *      i. Get the duplicate symbol 
	 *      ii remove the duplicate symobl from the list
	 *  4. check the database if all the symbols exists
	 *       i. if exists display message
	 *       ii else add to the database
	 *  5. response is sent to client
	 * @param stock
	 * @return
	 */
	@Operation(description="This end-point will add list stock passed in the POST body, only inserts if valid token is used and returns the status. "
			+ "Token is required for access. Use HTTP request header Authorization: Bearer xxx.yyy.zzz",
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= ApiAppResponse.class)))})
	@PostMapping("/add/stocks")
	public ResponseEntity<ApiAppResponse> addStocks(@RequestBody List<StockInfoWrapper> stockList, HttpServletRequest request){
		ApiAppResponse response ;
		try {
			String userName = validateTokenAndGetUserName(request);
			if(userName != null && stockList != null && !stockList.isEmpty()) {

				//for each item in the list perform the input validation, if anything is false then throw exception
				long inputInvalidShemaCheck = stockList.parallelStream().filter(a -> false == validateInputValues.apply(a)).count();
				if(inputInvalidShemaCheck > 0 ) {
					response = createResponse("Input json format NOT vaild.", Optional.of(new ArrayList<StockInfo>())); 
					return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);	
				}

				//Identify the duplicate from the input list
				List<String> duplicatedList = findDuplicateSymbols(stockList);
				// this is for displaying in the response
				String symbolsVal = "";
				if(!duplicatedList.isEmpty()) {
					symbolsVal = duplicatedList.stream().collect(Collectors.joining(","));
				}

				List<StockInfoWrapper> filteredInputList = filterDuplicateStockInfo(stockList);

				if(filteredInputList.isEmpty()) {
					throw new Exception("The input contains duplicate symbols "+symbolsVal +" , consolidate it.");
				}

				// Fetch the list of Stocks if already available in database
				List<String> stockInDatabase = filteredInputList.stream()
						.map(item -> queryDBForSymbols.apply(item.getSymbol(),userName))
						.filter(dbStockSymbol -> dbStockSymbol != null)
						.collect(Collectors.toList());

				// Create stock info object from stockinfo wrapper
				// insert only any new symbols that has been added in the input list
				List<StockInfo> stockInfoLst = filteredInputList.stream()
						.filter(item -> !stockInDatabase.contains(item.getSymbol()))
						.map(item -> new StockInfo(0, item.getSymbol().toUpperCase(), item.getAvgStockPrice(), item.getStockCount(), userName, true))
						.collect(Collectors.toList());
				if(stockInfoLst.isEmpty()) {
					response = createResponse("All Stocks are already exists in the database.", Optional.of(new ArrayList<StockInfo>()));
					return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);
				}
				List<StockInfo> stockInfoOutput = stockService.storeStocks(stockInfoLst);
				String status = "Successfully added stocks";
				if(!"".equals(symbolsVal)) {
					status += " - Duplicate symbols identified, not insterted to database - ["+symbolsVal+ "], Please consolidate it.";
				}
				response = createResponse(status, Optional.of(stockInfoOutput));
				return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);

			}else {
				throw new Exception("Input stock List is empty or Token might have been expired.");
			}

		}catch(Exception e) {
			log.error("Exception occurred when storing stock info", e);
			response = createResponse(e.getMessage(), Optional.of(stockList));
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	@Operation(description="This end-point will return the specific stock current price and other info."
			+ "Token is required for access. Use HTTP request header Authorization: Bearer xxx.yyy.zzz",
			parameters = {@Parameter(in= ParameterIn.PATH, name = "symbol", description = "The stock symbol per the standard")},
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= ComputeStockMetrics.class)))})
	@GetMapping("/{symbol}")
	public ResponseEntity<?> getStockDetails(@PathVariable("symbol") String symbol, HttpServletRequest request) throws IOException {

		ComputeStockMetrics stockInfo = new ComputeStockMetrics();
		String userName = validateTokenAndGetUserName(request);
		if(userName == null) {
			return new ResponseEntity<>("User name doesn't exists or Token Expired",HttpStatus.INTERNAL_SERVER_ERROR);   
		}
		StockWrapper stock =  computeStockMetricsService.getStockPrice(symbol);

		stockInfo.setSymbol(stock.getStock()!=null?stock.getStock().getSymbol():"Symbol Not available.");
		stockInfo.setCurrentPrice(stock.getPrice().floatValue());
		stockInfo.setCompanyName(stock.getStock()!=null?stock.getStock().getName():"Name Not available.");
		stockInfo.setLastAccessed(stock.getStock()!=null?stock.getLastAccess():LocalDateTime.now());
		return new ResponseEntity<>(stockInfo,HttpStatus.OK);
	}

	@Operation(description="This end-point will return the stock information computed for the invested amount and returns details as response."
			+ "Token is required for access. Use HTTP request header Authorization: Bearer xxx.yyy.zzz",
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= ComputedStockOuputWrapper.class)))})
	@PostMapping("/stock-info")
	public ResponseEntity<?> getComputedStockDetails(HttpServletRequest request) {

		String userName = validateTokenAndGetUserName(request);
		ComputedStockOuputWrapper output = new ComputedStockOuputWrapper();
		if(userName == null) {
			output.setSimpleStatus("Exception occured computing or Token expired");
			return new ResponseEntity<>(output,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if(userName != null) {
			output =  computeStockMetricsService.getInvsetedStockInfo(userName);
		}

		//passing the list to get the list of info
		return new ResponseEntity<>(output,HttpStatus.OK);
	}


	private final static String FORCE_DELETE = "force";
	private final static String ALL_SYMBOLS = "all-symbols";

	/**
	 * Function to delete the stock info from data base completely
	 * @param symbol
	 * @param force
	 * @return
	 */
	@Operation(description="This end-point will delete specific stock when valid symbol is passed as parameter, with /force parameter the stock will be deleted from database."
			+ "Token is required for access. Use HTTP request header Authorization: Bearer xxx.yyy.zzz",
			parameters = {@Parameter(in= ParameterIn.PATH, name = "symbol", required = true, description = "The stock symbol per the standard. Eg: MSFT for Microsoft, all-symbols/force to delete all stock per user"),
					@Parameter(in= ParameterIn.PATH, name = "force",required = false, description = "Optional value, when provided deletes the stock symbol completely from database.")},
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= ApiAppResponse.class)))})
	@DeleteMapping("/delete/{symbol}/{force}")
	public ResponseEntity<ApiAppResponse> deleteStock(@PathVariable("symbol") String symbol,@PathVariable("force") String force, HttpServletRequest request){
		ApiAppResponse response = null;
		StockInfo stock = null;
		try {
			boolean forceDelete= false;
			boolean deleteAll = false;
			String userName = validateTokenAndGetUserName(request);
			if(userName == null) {
				throw new Exception("User name not exits or Token Expired.");
			}
			if(userName != null && symbol != null && force !=null) {
				// check if we need to delete all symbols
				if(ALL_SYMBOLS.equalsIgnoreCase(symbol.trim()) && FORCE_DELETE.equals(force.trim())) {
					stockService.deleteAllStockInfo(userName);
					response = createResponse("Stock Symbol not exits in datasource.",Optional.of(new ArrayList<StockInfo>()));
					deleteAll = true;
				}else {
					stock = stockService.getStockInfoBySymbolAndUser(symbol,userName);
					if(stock != null) {
						//perform soft delete
						if(force != null && FORCE_DELETE.equals(force.trim())) {
							forceDelete=true;
						}
						if(forceDelete) {
							//completed remove the symbol from database
							stockService.deleteStockInfo(symbol,userName);	
						}else {
							// deactivate the symbol in database
							stockService.softDeleteStockInfo(symbol,userName,true);
						}
					}
				}
			}else {
				response = createResponse("Symbol "+symbol+" NOT Exits.",Optional.of(new ArrayList<StockInfo>()));
				return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);
			}
			if(stock!=null) {
				response = createResponse("Successfully deleted",Optional.of(stock));
			}else { 
				//In case of delete all the stock input is null, since all stock for user will be deleted
				if(!deleteAll) {
  				   response = createResponse("Stock Symbol not exits in datasource.",Optional.of(new ArrayList<StockInfo>()));
				}
			}
			return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);
		}catch(Exception e) {
			log.error("Exception occurred when deleting stock info using symbol", e);
			response = createResponse("Exception occured deleting "+symbol+" "+e.getMessage(),Optional.of(new ArrayList<StockInfo>()));
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(description="This end-point will update list of stock for valid symbol in input list, and if there is any new symbol gets added to db, update to count or average price will be updated in db."
			+ "Token is required for access. Use HTTP request header Authorization: Bearer xxx.yyy.zzz",
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= ApiAppResponse.class)))})
	@PutMapping("/update/stocks")
	public ResponseEntity<ApiAppResponse> updateStocks(@RequestBody List<StockInfoWrapper> stockList, HttpServletRequest request){

		// Duplicate code similar to add stock, re-factor if possible latter.
		ApiAppResponse response ;
		try {
			String userName = validateTokenAndGetUserName(request);
			if(userName != null && stockList != null && !stockList.isEmpty()) {

				//for each item in the list perform the input validation, if anything is false then throw exception
				long inputInvalidShemaCheck = stockList.parallelStream().filter(a -> false == validateInputValues.apply(a)).count();
				if(inputInvalidShemaCheck > 0 ) {
					response = createResponse("Input json format NOT vaild.", Optional.of(new ArrayList<StockInfo>())); 
					return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);	
				}

				//Identify the duplicate from the input list
				List<String> duplicatedList = findDuplicateSymbols(stockList);
				// this is for displaying in the response
				String symbolsVal = "";
				if(!duplicatedList.isEmpty()) {
					symbolsVal = duplicatedList.stream().collect(Collectors.joining(","));
				}

				List<StockInfoWrapper> filteredInputList = filterDuplicateStockInfo(stockList);

				if(filteredInputList.isEmpty()) {
					throw new Exception("The input contains duplicate symbols "+symbolsVal +" , consolidate it.");
				}

				// Fetch the list of Stocks if already available in database
				List<StockInfo> stockInfoFromDatabase = stockService.getStocksDetail(userName);

				//fetching the list of stock info from DB, below code will create a list of string symbols
				// this is used for filtering out the new stock symbols from input stock list
				List<String> stockInDatabase = stockInfoFromDatabase.parallelStream().map(StockInfo::getSymbol).collect(Collectors.toList());

				// Create stock info object from stockinfo wrapper
				// insert only any new symbols and stocks that updated that has been added in the input list
				List<StockInfo> newStockInfoSymbolFromInputLst = filteredInputList.stream()
						.filter(item -> !stockInDatabase.contains(item.getSymbol()))
						.map(item -> new StockInfo(0, item.getSymbol().toUpperCase(), item.getAvgStockPrice(), item.getStockCount(), userName, true))
						.collect(Collectors.toList());

				if(newStockInfoSymbolFromInputLst.isEmpty()) {
					//response = createResponse("All Stocks are already upto date in the database.", Optional.of(new ArrayList<StockInfo>()));
					//return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);
				}
				List<StockInfo> stockInfoOutput = new ArrayList<>();
				if(!newStockInfoSymbolFromInputLst.isEmpty()) {
				  stockInfoOutput = stockService.storeStocks(newStockInfoSymbolFromInputLst);
				}

				//Check if there are any updates in the input stock list stock count or avg value and update it in db
				List<StockInfo> inputStockInfoExistsInDBToIdentifyChanges = filteredInputList.stream()
						.filter(item -> stockInDatabase.contains(item.getSymbol()))
						.map(item -> new StockInfo(0, item.getSymbol().toUpperCase(), item.getAvgStockPrice(), item.getStockCount(), userName, true))
						.collect(Collectors.toList());
				
				//Pass the input stock list, as first argument, then pass the stock list from db, so it will filter
				// and provide the input list filtered out.
				List<StockInfo> stockToBeUpdateList = filterInputAndDBStock.apply(inputStockInfoExistsInDBToIdentifyChanges,stockInfoFromDatabase);
				if(stockToBeUpdateList!=null && !stockToBeUpdateList.isEmpty()) {
					stockInfoOutput.addAll(stockToBeUpdateList);
					stockToBeUpdateList.forEach(stockInfoItem -> {
						try {
							stockService.updateStock(stockInfoItem);
						} catch (Exception e) {
							throw new RuntimeException("Exception occurred in updating the stock."+e.getMessage(),e); 
						}
					});
				}

				String status = "Successfully updates the stock info";
				if(!"".equals(symbolsVal)) {
					status += " - Duplicate symbols identified, not insterted to database - ["+symbolsVal+ "], Please consolidate it.";
				}
				response = createResponse(status, Optional.of(stockInfoOutput));
				return new ResponseEntity<ApiAppResponse>(response,HttpStatus.OK);

			}else {
				throw new Exception("Input stock List is empty or Token might have been expired.");
			}

		}catch(Exception e) {
			log.error("Exception occurred when storing stock info", e);
			response = createResponse(e.getMessage(), Optional.of(stockList));
			return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	BinaryOperator<List<StockInfo>> filterInputAndDBStock = (stockListFromInput, stockListFromDB)->{
		//override the stock info equals and hash object using lombok 
	    //Based on the hash and equals on the included field on the object
		List<StockInfo> result = stockListFromInput.stream()
				//.peek(itm -> System.err.println("BEFORE:- "+itm.getSymbol()+"|"+itm.getStockCount()+"|"+itm.getAvgStockPrice()))
				.filter(item -> !stockListFromDB.contains(item))
				//.peek(itm -> System.out.println("AFTER:- "+itm.getSymbol()+"|"+itm.getStockCount()+"|"+itm.getAvgStockPrice()))
				.collect(Collectors.toList());
		
		return result;
	};

	
	public BinaryOperator<List<StockInfo>> getFilterInputAndDBStock() {
		return filterInputAndDBStock;
	}


	// check if the list of symbols already exists in the database
	BiFunction<String,String, String> queryDBForSymbols = (stockSymbol,userNameValue) -> {
		StockInfo stockFromDB;
		try {
			stockFromDB = stockService.getStockInfoBySymbolAndUser(stockSymbol, userNameValue);
		} catch (Exception exe) {
			log.error("Exception in fetching data from db part of bifunction. ",exe);
			return null;
		}
		return stockFromDB != null?stockFromDB.getSymbol():null;
	};

	// check if the list of symbols already exists in the database
	/*
	Function<String,List<StockInfo>> queryDBForStockInfo = (userNameValue) -> {
		List<StockInfo> stockFromDB;
		try {
			stockFromDB = stockService.getStocksDetail(userNameValue);
		} catch (Exception e) {
			log.error("Exception in fetching data from db part of bifunction. ",e);
			return null;
		}
		return stockFromDB;
	};
	*/
	/**
	 * Below method is used to validate the user info from the token passed
	 * This check is already done by the jwt filter, additional validation done again
	 * @param request
	 * @return
	 */
	protected String validateTokenAndGetUserName(HttpServletRequest request) {

		final String authorizationHeader = request.getHeader("Authorization");
		String jwtToken = null;
		String tokenUserName = null;
		boolean isValidToken = false;
		boolean issueInAccess = false;
		if( authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
			jwtToken = authorizationHeader.substring(7);
			tokenUserName = jwtService.extractUserName(jwtToken);
			if(tokenUserName != null && jwtToken != null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(tokenUserName);
				isValidToken = jwtService.validateToken(jwtToken,userDetails);
				//if the username is not present in database and didn't match the jwt token - report issue
				if(userDetails == null || !userDetails.getUsername().equals(tokenUserName)) {
					issueInAccess = true;
				}
			}else {
				issueInAccess = true;
			}
		}else {
			issueInAccess = true;
		}

		if(!issueInAccess && isValidToken) {
			return tokenUserName;
		}else {
			return null;
		}
	}

	/**
	 * If the controller throws nosuchelement exception handler it here 
	 * Below is only used in case if we are using Spring Data rest
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NoSuchElementException.class)
	public String return404Error(NoSuchElementException e) {
		log.error("Exception occured during process.");
		return e.getMessage();
	}
}

