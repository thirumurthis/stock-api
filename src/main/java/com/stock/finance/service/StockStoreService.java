package com.stock.finance.service;

import java.util.List;

import com.stock.finance.model.StockInfo;

public interface StockStoreService {

	List<StockInfo> getStocksDetail(String userName) throws Exception;
	StockInfo storeStockInfo(StockInfo stockInfo) throws Exception;
	List<StockInfo> storeStocks(List<StockInfo> stocks ) throws Exception;
	StockInfo getStockInfoBySymbolAndUser(String symbol,String userName) throws Exception;
	void softDeleteStockInfo(String symbol,String userName, boolean softdelete) throws Exception;
	void deleteStockInfo(String symbol,String userName) throws Exception;
	void deleteAllStockInfo(String userName) throws Exception;
	StockInfo save(StockInfo stock) throws Exception;
	
}
