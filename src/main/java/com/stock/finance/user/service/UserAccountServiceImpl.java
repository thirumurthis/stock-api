package com.stock.finance.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.stock.finance.user.model.Users;
import com.stock.finance.user.repo.UserRepository;

public class UserAccountServiceImpl implements UserAccountService{

	@Autowired
	UserRepository userRepo;
	
	PasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Users saveUser(Users user) throws Exception{
		user.setPassword(encoder.encode(user.getPassword()));
		Users userInfo =  userRepo.save(user);
		return userInfo;
	}

	@Override
	public String getUserNameInfo(String userName) throws Exception{
		Users user = userRepo.findByUserName(userName);
		if(user != null) {
		   return user.getUserName();
		}else {
			return null;
		}
	}

	@Override
	public Users getUserInfo(String userName, String apiKey) throws Exception {
		Users user = userRepo.findByUserNameAndApiKey(userName, apiKey);
		return user;
	}

	@Override
	public Users getUserUsingApiKey(String apiKey) throws Exception {
		Users user = userRepo.findByApiKey(apiKey);
		return user;
	}

}
