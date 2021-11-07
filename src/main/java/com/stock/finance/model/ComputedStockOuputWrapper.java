package com.stock.finance.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ComputedStockOuputWrapper {

	private List<ComputeStockMetrics> stockInfo;
	private float investedAmount;
	private float currentMarketTotalAmount;
	private float difference;
	private String profitLossStatus;
	private LocalDateTime lastAccessed;
	private String simpleStatus;
}

