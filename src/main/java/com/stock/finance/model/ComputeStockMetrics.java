package com.stock.finance.model;


import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ComputeStockMetrics {

	private String symbol;
	private float stockCount;
	private float currentPrice;
	private String companyName;
	private LocalDateTime lastAccessed;
	private float currentInvestedAmount;
	private float actualInvestedAmount;
	private float difference;
	private String profitOrLoss;
}
