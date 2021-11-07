package com.stock.finance.controller;

import java.net.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.finance.model.api.SimpleStatusResponse;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.user.service.UserAccountService;

@RestController
@RequestMapping("/admin")
public class AdminViewController {

	@Autowired
	private UserAccountService userService;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired 
	JWTManagerService jwtService;
	
	@GetMapping("/user/{username}")
	public ResponseEntity<SimpleStatusResponse> getUserInfo(@PathVariable("username")String userName,HttpServletRequest request){

		final String authorizationHeader = request.getHeader("Authorization");
		String jwtToken = null;
		String tokenUserName = null;
		boolean isValidToken = false;
		boolean issueInAccess = false;
		if( authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
			jwtToken = authorizationHeader.substring(7);
			tokenUserName = jwtService.extractUserName(jwtToken);
			if(tokenUserName != null && jwtToken != null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(tokenUserName);
				isValidToken = jwtService.validateToken(jwtToken,userDetails);
				if(userDetails== null || !userDetails.getUsername().equals(tokenUserName)) {
					issueInAccess = true;
				}
			}
		}else {
			issueInAccess = true;
		}

		if(!issueInAccess && isValidToken && userName != null || !"".equals(userName) && userName.equals(tokenUserName)) {
			String userDetailInfo = userService.getUserNameInfo(userName);
			return new ResponseEntity<>(new SimpleStatusResponse(userDetailInfo),HttpStatus.OK);
		}else {
			return new ResponseEntity<>(new SimpleStatusResponse("You don't have access to this end-point"),HttpStatus.OK);					
		}
	}
}
