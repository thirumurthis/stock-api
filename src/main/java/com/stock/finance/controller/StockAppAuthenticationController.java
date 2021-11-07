package com.stock.finance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.finance.model.AuthenticationRequest;
import com.stock.finance.model.api.AuthenticationResponse;
import com.stock.finance.model.api.SimpleStatusResponse;
import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.user.model.Users;
import com.stock.finance.user.service.UserAccountService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/stock-app/")
@Log4j2
public class StockAppAuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTManagerService jwtManagerService;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Autowired
	private UserAccountService userService;
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> generateAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{

	 try{
			//if below authentication we need to catch and throw exception
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(),authenticationRequest.getPassword()));
		} catch (BadCredentialsException e){
			return new ResponseEntity<>(new SimpleStatusResponse("Invalid username or password"),HttpStatus.OK);
			//throw new Exception ("Invalid username and password");
		}
		// since now validated we need to get the username and generate JWT
		// autowired the service
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUserName());
		final String jwt = jwtManagerService.generateToken(userDetails);
		if(jwt == null || userDetails == null) {
			return new ResponseEntity<>(new SimpleStatusResponse("Username or password doesn't exists."),HttpStatus.OK);
		}
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> signUpUser(@RequestBody AuthenticationRequest userInfo) {
		if(userInfo != null) {
			Users user = new Users();
			user.setUserName(userInfo.getUserName());
			user.setPassword(userInfo.getPassword());
			user.setActive(true);
			//only for debugging purpose
			if("thiru123".equalsIgnoreCase(user.getUserName())) {
				user.setRoles("ROLE_ADMIN");
			}else {
				user.setRoles("ROLE_USER");
			}
       		String storedUserName = userService.saveUser(user);
       		log.info("Singup endpoint invoked with user info "+user.getUserName());
       		return new ResponseEntity<>(new SimpleStatusResponse("Welcome "+ storedUserName +" !!!, Successfully singed up!!"),HttpStatus.OK); 
		}else {
       		log.info("/singup endpoint invoked, the user info object is null");
			return new ResponseEntity<>(new SimpleStatusResponse("Signup form didn't successfully store user info."),HttpStatus.OK);	
		}
	}	
}
