package com.stock.finance.user.service;

import com.stock.finance.user.model.Users;

public interface UserAccountService {

	public String saveUser(Users user) throws Exception;
	public String getUserNameInfo(String userName) throws Exception;
}
