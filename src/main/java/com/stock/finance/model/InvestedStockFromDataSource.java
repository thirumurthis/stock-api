package com.stock.finance.model;

import lombok.Data;

@Deprecated
@Data
public class InvestedStockFromDataSource {
	private String symbol;
	private float stockCount;
	private float averagePrice;
}
