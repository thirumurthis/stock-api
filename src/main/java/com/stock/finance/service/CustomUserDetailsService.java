package com.stock.finance.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	PasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails userDetail = User.builder().username("user")
									.password(encoder.encode("user"))
									.authorities("USER")
									.build();
		System.out.println(userDetail.getPassword());
		if(!userDetail.getUsername().equals(username)) {
			throw new UsernameNotFoundException("User not Found");
		}
		return userDetail;
	}
}
