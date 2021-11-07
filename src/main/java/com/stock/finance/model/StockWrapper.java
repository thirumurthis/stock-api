package com.stock.finance.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;
import yahoofinance.Stock;

@Getter
@With
@AllArgsConstructor
public class StockWrapper {
	
	private Stock stock;
	private LocalDateTime lastAccess;
	
	public StockWrapper(final Stock stock) {
	  this.stock= stock;
	  lastAccess = LocalDateTime.now();
	}

	public BigDecimal getPrice() throws IOException{
		return getStock()!=null?getStock().getQuote(true).getPrice():new BigDecimal(0.0);
	}
}
