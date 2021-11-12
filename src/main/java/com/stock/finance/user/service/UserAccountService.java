package com.stock.finance.user.service;

import com.stock.finance.user.model.Users;

public interface UserAccountService {

	public Users saveUser(Users user) throws Exception;
	public Users getUserInfo(String userName, String apiKey) throws Exception;
	public Users getUserUsingApiKey(String apiKey) throws Exception;
	public String getUserNameInfo(String userName) throws Exception;
}
