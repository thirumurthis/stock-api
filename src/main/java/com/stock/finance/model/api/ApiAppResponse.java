package com.stock.finance.model.api;

import java.util.List;

import com.stock.finance.model.StockInfo;
import com.stock.finance.model.StockInfoWrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiAppResponse {

	private String status;
	private List<StockInfoWrapper> stockInfo;
	
}
