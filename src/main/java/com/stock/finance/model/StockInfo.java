package com.stock.finance.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="STOCK_STORE", 
       uniqueConstraints = @UniqueConstraint(
    		                  name = "symbol_unique", 
    		                  columnNames = {"stock_symbol","user_name"})
       )
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockInfo {

	@Id
	@SequenceGenerator(name="stock_sequence",
	                   sequenceName = "stock_sequence", 
	                   allocationSize = 1)
	// Not using auto since we are using Sequence generator (other option AUTO can also be used)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, 
	                generator = "stock_sequence")
	private int id;
	// added unique constraints and this field shouldn't be nullable.
	@Column(name="stock_symbol",nullable = false)
	private String symbol;
	@Column(name="average_price",nullable = false)
	private float avgStockPrice;
	@Column(name="stock_count",nullable = false)
	private float stockCount;
	@Column(name="user_name",nullable = false)
	private String userName;
	// active flag with default, when deleting issue a soft delete 
	// for forcible delete of row handle in controller
	@Column(name="active",nullable = false, columnDefinition = "boolean default true")
	private boolean active = true;
}
