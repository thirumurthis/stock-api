package com.stock.finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.finance.model.AuthenticationRequest;
import com.stock.finance.service.AuthenticationResponse;
import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.service.JWTManagerService;

@RestController
@RequestMapping("/stock-app/")
public class StockAppAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTManagerService jwtManagerService;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> generateAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{

	 try{
			//if below authentication we need to catch and throw exception
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(),authenticationRequest.getPassword()));
		} catch (BadCredentialsException e){
			throw new Exception ("Invalid username and password");
		}
		// since now validated we need to get the username and generate JWT
		// autowired the service
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUserName());
		final String jwt = jwtManagerService.generateToken(userDetails);
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
}
