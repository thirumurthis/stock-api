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
	public String saveUser(Users user) throws Exception{
		user.setPassword(encoder.encode(user.getPassword()));
		Users userInfo =  userRepo.save(user);
		return userInfo.getUserName();
	}

	@Override
	public String getUserNameInfo(String userName) throws Exception{
		Users user = userRepo.findByUserName(userName);
		return user.getUserName()+" :: "+user.getRoles();
	}

}
