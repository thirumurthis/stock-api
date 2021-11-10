package com.stock.finance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stock.finance.model.AuthenticationRequest;
import com.stock.finance.model.ComputeStockMetrics;
import com.stock.finance.model.api.AuthenticationResponse;
import com.stock.finance.model.api.SimpleStatusResponse;
import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.user.model.Users;
import com.stock.finance.user.service.UserAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

	@Operation(description="This end-point is used to provide the token for when user name and password passed in POST request body.",
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= AuthenticationResponse.class)))})
	@PostMapping("/token")
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
	
	@Value("${admin.user.info:secretadmin}")
	private String adminUserInfo;
	
	@Operation(description="This end-point is used to register or sign-up the user to access the API.",
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= String.class)))})
	@PostMapping("/signup")
	public ResponseEntity<?> signUpUser(@RequestBody AuthenticationRequest userInfo) {
		try {
			if(userInfo != null) {
				//check if the username is already registered
				if(userInfo.getUserName()!=null || !"".equals(userInfo.getUserName())) {
					String userFromDB =userService.getUserNameInfo(userInfo.getUserName());
					if(userFromDB!=null && userFromDB.contains(userInfo.getUserName())) {
						return new ResponseEntity<>(new SimpleStatusResponse("User Name : "+ userInfo.getUserName() +" already exists in database!!"),HttpStatus.OK);
					}
				}
				//else create a new user and save in database
				Users user = new Users();
				user.setUserName(userInfo.getUserName());
				user.setPassword(userInfo.getPassword());
				user.setActive(true);
				//only for debugging purpose
				if(adminUserInfo.equalsIgnoreCase(user.getUserName())) {
					user.setRoles("ROLE_ADMIN");
				}else {
					user.setRoles("ROLE_USER");
				}
				String storedUserName = userService.saveUser(user);
				log.info("Singup endpoint invoked with user info "+user.getUserName());
				return new ResponseEntity<>(new SimpleStatusResponse("Welcome "+ storedUserName +" !!!, Successfully singed up!!"),HttpStatus.OK); 
			}else {
				log.warn("/singup endpoint invoked, the user info object is null");
				return new ResponseEntity<>(new SimpleStatusResponse("Signup form didn't successfully store user info."),HttpStatus.OK);	
			}
		}catch(Exception e) {
			log.error("/singup endpoint invoked, exception occured ",e);
			return new ResponseEntity<>(new SimpleStatusResponse("Signup form didn't successfully store user info."),HttpStatus.INTERNAL_SERVER_ERROR);	
		}
	}	
}
