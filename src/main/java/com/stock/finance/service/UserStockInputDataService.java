package com.stock.finance.service;

import java.util.List;

import com.stock.finance.model.InvestedStockFromDataSource;

public interface UserStockInputDataService {

	public List<InvestedStockFromDataSource> getInputAsList(InvestedStockFromDataSource[] inputStockInfo);
}
