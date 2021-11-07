package com.stock.finance.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.stock.finance.user.repo.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	//PasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Autowired
	UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//fetch information from the database
		com.stock.finance.user.model.Users user = userRepo.findByUserName(username);
		
		List<GrantedAuthority> authorities = Arrays.asList(user.getRoles().split(",")).stream().map(auth -> new SimpleGrantedAuthority(auth.toString())).collect(Collectors.toList());
		//UserDetails userDetail = new CustomUserDetails(user.getUserName(), user.getPassword(), user.isActive(), null);
		UserDetails userDetail = User.builder().username(user.getUserName())
				.password(user.getPassword())
				.authorities(authorities)
						//user.getAuthorities().stream().map(auth -> new SimpleGrantedAuthority(auth.toString())).collect(Collectors.toList()))
				.build();
		/*UserDetails userDetail = User.builder().username("user")
									.password(encoder.encode("user"))
									.authorities("USER")
									.build();
		System.out.println(userDetail.getPassword());
		*/
		if(!userDetail.getUsername().equals(username)) {
			throw new UsernameNotFoundException("User not Found");
		}
		return userDetail;
	}
}
