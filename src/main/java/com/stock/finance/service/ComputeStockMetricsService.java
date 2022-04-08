package com.stock.finance.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.finance.model.ComputeStockMetrics;
import com.stock.finance.model.ComputedStockOuputWrapper;
import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockInfoWrapper;
import com.stock.finance.model.StockWrapper;

import lombok.extern.log4j.Log4j2;
import yahoofinance.YahooFinance;

@Service
@Log4j2
public class ComputeStockMetricsService {
	
	@Autowired
	private StockStoreService stockStoreService;
	
	/**
	 * This method fires query using yahoo finance api and fetch the stock price
	 * @param symbol
	 * @return
	 */
	public StockWrapper getStockPrice(String symbol) { 
		StockWrapper stock = null;
		try {
  		 stock = new StockWrapper(YahooFinance.get(symbol));
  		 return stock;
		}catch(IOException io) {
			log.error("IO Exception occured"+io);
			stock = new StockWrapper(null);
		}
		return stock;
	}

	/**
	 * this method converts the yahoo finance stock info to custom stock info object
	 * @param symbol
	 * @return
	 */
	public ComputeStockMetrics getStockInfo(String symbol) {
		
		StockWrapper stock;
		ComputeStockMetrics stockInfo = new ComputeStockMetrics();
		try {
  		 stock = new StockWrapper(YahooFinance.get(symbol));
  		 if(stock.getStock() != null) {
  			 stockInfo.setCompanyName(stock.getStock().getName());
  			 stockInfo.setCurrentPrice(stock.getPrice().floatValue());
  			 stockInfo.setLastAccessed(stock.getLastAccess());
  		 }else{
 			stockInfo.setCompanyName("Name Not Available");
 			stockInfo.setSymbol(symbol+ " - Not Available");
 			stockInfo.setCurrentPrice(0.0f);
  		 }
		}catch(IOException io) {
			log.error("IO Exception occured"+io);
			stockInfo.setCompanyName("Company name - Not Available");
			stockInfo.setSymbol(symbol+ " - Not Available");
			stockInfo.setCurrentPrice(0.0f);
		}
		return stockInfo;
	}

      public ComputedStockOuputWrapper getInvsetedStockInfo(String userName) {

    	  ComputedStockOuputWrapper stocks = new ComputedStockOuputWrapper();
    	try {
    		//InvestedStockDetails[] investedStockArray = dataInputService.readFromInputJson(inputFileName);
    		//List<InvestedStockDetails> investedStockList = dataInputService.getInputAsList(investedStockArray);
    		List<StockInfo> stockInfoListFromDb;
			stockInfoListFromDb = stockStoreService.getStocksDetail(userName);
    		
    		List<StockInfoWrapper> stockInfoWrapperList = stockInfoListFromDb.stream()
    							.map(item -> new StockInfoWrapper(item.getSymbol(), item.getAvgStockPrice(), item.getStockCount()))
    							.collect(Collectors.toList());
    		computeAndFormulateStockOutputInfo(stocks, stockInfoWrapperList);
    		stocks.setSimpleStatus("Successfully computed.");

    	} catch (Exception e) {
    		log.error("getIinvestedStockInfo. IOException. ",e);
    		//in case of error just send blank object
    		stocks = new ComputedStockOuputWrapper();
    		stocks.setSimpleStatus("Failed during computation");
    	}

    	return stocks;
    }

	private void computeAndFormulateStockOutputInfo(ComputedStockOuputWrapper stocks, List<StockInfoWrapper> investedStockList) {
		List<ComputeStockMetrics> stockDetails = getLatestStockDetails(investedStockList);
		stocks.setStockInfo(stockDetails);
		//Get the total invested price
		float investedAmount = getActualTotalInvestedStockAmount(investedStockList);
			
		stocks.setInvestedAmount(investedAmount);
		stocks.setCurrentMarketTotalAmount(getLatestTotalInvestedStockAmount(stocks));
		stocks.setProfitLossStatus((stocks.getCurrentMarketTotalAmount()-stocks.getInvestedAmount())>0.0f?"**PROFIT**":"**LOSS**");
		stocks.setDifference(stocks.getCurrentMarketTotalAmount()-stocks.getInvestedAmount());
		stocks.setLastAccessed(LocalDateTime.now());
	}

      /**
       * Iterate the stock details and for each stock symbol
       * 1. get the latest price for the symbol
       * 2. set the current invested price, = (# of stock) * (latest price of stock symbol)
       * 3. set the actual invested price, = (average price input) * (# of stocks input)
       * 4. set the difference, =  (current invested price) - (actual invested price)
       * 5. determine profit/loss per stock symbol : difference > 0 Profit, else Loss
       * 
       * @param investedStock
       * @return
       */
    public List<ComputeStockMetrics> getLatestStockDetails(List<StockInfoWrapper> investedStock) {
    	List<ComputeStockMetrics> stockInfoList = new ArrayList<>();
    	investedStock.stream().forEach(element -> {
			try {
				StockWrapper stockWrapper = getStockPrice(element.getSymbol());
				ComputeStockMetrics stockDetails = new ComputeStockMetrics();
				stockDetails.setLastAccessed(LocalDateTime.now());
				stockDetails.setSymbol(element.getSymbol());
				stockDetails.setStockCount(element.getStockCount());
				//get the latest price of the stock symbol
				stockDetails.setCurrentPrice(stockWrapper!=null?stockWrapper.getPrice().floatValue():0.0f);
				stockDetails.setCompanyName(
						stockWrapper!=null?(stockWrapper.getStock()!=null?stockWrapper.getStock().getName():"Name Not Available"):"Name Not Available" );
				//latest invested amount per share 
				float currentInvestedPerShare = element.getStockCount() * stockDetails.getCurrentPrice();
				stockDetails.setActualInvestedAmount(element.getAvgStockPrice()*element.getStockCount());
				stockDetails.setCurrentInvestedAmount(currentInvestedPerShare);
				float difference = currentInvestedPerShare-(element.getAvgStockPrice()*element.getStockCount());
				stockDetails.setDifference(difference);
				// if difference is +ve profit else loss
				stockDetails.setProfitOrLoss(difference>0?"** Profit **":"** Loss **");
				stockInfoList.add(stockDetails);
			} catch (IOException e) {
				log.error("Exception occurred when fetching price :"+e);
				e.printStackTrace();
			}
		});
    	return stockInfoList;
    }
    
    /**
     * This method will provide the total price of current market for the user provided stock list
     * @param stockDetails
     * @return
     */
    private float getLatestTotalInvestedStockAmount(final ComputedStockOuputWrapper stockDetails) {
    	return stockDetails.getStockInfo().stream()
    	.map(e-> e.getCurrentInvestedAmount())
    	.reduce(0.0f,(a,b)->(a+b));
    }
    
    /**
     * This method fetch the total amount based on the passed list with stock info
     * @param input
     * @return
     */
    private float getActualTotalInvestedStockAmount(final List<StockInfoWrapper> input) {
    	float total = 
    			input.stream()
    			.map(element -> element.getAvgStockPrice() * element.getStockCount())
		        .reduce(0.0f,(a,b)->(a+b));
    	return total;
    }
    
}
