package com.stock.finance.model.api;

import java.util.List;

import com.stock.finance.model.StockInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {

	private String status;
	private List<StockInfo> stockInfo;
	
}
