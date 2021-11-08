package com.stock.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockStoreRepository;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StockStoreServiceImpl implements StockStoreService{

	@Autowired
	StockStoreRepository stockStoreRepository;
	
	@Override
	public List<StockInfo> getStocksDetail(String userName) throws Exception{
		return stockStoreRepository.findByUserName(userName);
	}

	@Override
	public StockInfo storeStockInfo(StockInfo stockInfo) throws Exception{
		if(stockInfo != null && stockInfo.getSymbol() != null) {
		  return stockStoreRepository.save(stockInfo);
		}else {
			log.error("[StockStoreServiceImpl:storeStockInfo] StockInfo object either null or stock symbol null");
			//if in case stock object is empty with no symbol then throw exception
		  throw new Exception("[StockStoreServiceImpl:storeStockInfo] StockInfo object either null or stock symbol null");	
		}
	}

	@Override
	public void softDeleteStockInfo(String symbol,String userName, boolean softDelete) throws Exception {
		if(null != symbol && !symbol.equals("")) {
  		   stockStoreRepository.softDeleteStockInfo(symbol,userName,softDelete);
		}else {
			log.error("[StockStoreServiceImpl:deleteStockInfo] stock symbol is null");
			throw new Exception("[StockStoreServiceImpl:deleteStockInfo] stock symbol is null");
		}
	}
	
	@Override
	public void deleteStockInfo(String symbol, String userName) throws Exception {
		if(null != symbol && !symbol.equals("")) {
  		   stockStoreRepository.deleteStockInfo(symbol,userName);
		}else {
			log.error("[StockStoreServiceImpl:deleteStockInfo] stock symbol is null");
			throw new Exception("[StockStoreServiceImpl:deleteStockInfo] stock symbol is null");
		}
	}

	@Override
	public List<StockInfo> storeStocks(List<StockInfo> stocks) throws Exception {
		if(stocks != null && !stocks.isEmpty()) {
			List<StockInfo> result = stockStoreRepository.saveAll(stocks);
			return result;
		}else {
			log.error("[StockStoreServiceImpl:storeStocks] stock list is empty or null");
			throw new Exception ("[StockStoreServiceImpl:storeStocks] stock list is empty or null");
		}
		
	}

	@Override
	public StockInfo getStockInfoBySymbolAndUser(String symbol, String userName) throws Exception {
		
		return stockStoreRepository.findBySymbolAndUserName(symbol, userName);
	}

}
