package com.stock.finance.service;

import java.util.List;

import com.stock.finance.model.StockInfo;

public interface StockStoreService {

	List<StockInfo> getStocksDetail() throws Exception;
	StockInfo storeStockInfo(StockInfo stockInfo) throws Exception;
	List<StockInfo> storeStocks(List<StockInfo> stocks ) throws Exception;
	StockInfo getStockInfo(String symbol) throws Exception;
	void softDeleteStockInfo(String symbol,boolean softdelete) throws Exception;
	void deleteStockInfo(String symbol) throws Exception;
}
