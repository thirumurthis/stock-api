package com.stock.finance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockInfoWrapper {
	private String symbol;
	private float avgStockPrice;
	private float stockCount;
}
