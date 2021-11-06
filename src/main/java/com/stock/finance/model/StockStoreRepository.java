package com.stock.finance.model;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface StockStoreRepository extends JpaRepository<StockInfo, Integer>{

	//List<StockInfoFromStore> findById(int id);
	//List<StockInfoFromStore> findAll();
	
	//custom JPA method based query to fetch data based on symbol
	StockInfo findBySymbol(String stock_symbol);
	
	//update query to modify records
	// @Modifying annoation needs to be provided when we update/delete records in DB
	@Modifying
	// note transaction will add being and end also commit after update operation
	// ideally if we use this annotation at the service layer, we can perform group of operation
	@Transactional
	@Query(value = "update STOCK_STORE set stock_count = :count, average_price = :average_cost where stock_symbol = :symbol",
	       nativeQuery = true)
	int updateStockCountAveragePrice(@Param("symbol") String symbol,@Param("count") int count, @Param("average_cost") float average_cost);
	
	
	@Modifying
	@Transactional
	// using positional parameter for mapping the value
	@Query(value="delete from STOCK_STORE where stock_symbol = ?1 ",nativeQuery = true)
	void deleteStockInfo(String stockSymbol);
	
	@Modifying
	@Transactional
	// using positional parameter for mapping the value
	@Query(value="update STOCK_STORE set active = ?2 where stock_symbol = ?1 ",nativeQuery = true)
	void softDeleteStockInfo(String stockSymbol,boolean isActive);

}
