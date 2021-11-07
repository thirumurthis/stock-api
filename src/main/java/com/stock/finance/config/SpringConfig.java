package com.stock.finance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.stock.finance.filter.JwtRequestFilter;
import com.stock.finance.user.service.UserAccountService;
import com.stock.finance.user.service.UserAccountServiceImpl;

@Configuration
public class SpringConfig {

	 @Bean 
	 public JwtRequestFilter jwtRequestFilter() {
		 return new JwtRequestFilter(); 
	 }
	 
	 @Bean
	 public UserAccountService userAccountService() {
		 return new UserAccountServiceImpl();
	 }
}
