package com.stock.finance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stock.finance.filter.JwtRequestFilter;

@Configuration
public class SpringConfig {

	 @Bean 
	 public JwtRequestFilter jwtRequestFilter() {
		 return new JwtRequestFilter(); 
	 }
}
