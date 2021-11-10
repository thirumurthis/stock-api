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

import com.stock.finance.model.api.ApiAppResponse;
import com.stock.finance.model.api.SimpleStatusResponse;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.user.service.UserAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/admin")
@Log4j2
public class AdminViewController {

	@Autowired
	private UserAccountService userService;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired 
	JWTManagerService jwtService;
	
	@Operation(description="This end-point will display the username and role information from the database."
			+ "Token is required for access. Use HTTP request header Authorization: Bearer xxx.yyy.zzz",
			parameters = {@Parameter(in= ParameterIn.PATH, name = "username", required = true, description = "pass the user name information used to sign-up for API")},
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= SimpleStatusResponse.class)))})
	@GetMapping("/user/{username}")
	public ResponseEntity<SimpleStatusResponse> getUserInfo(@PathVariable("username")String userName,HttpServletRequest request){

		try {
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
		catch(Exception e) {
			log.error("user name endpoint error occured ",e);
			return new ResponseEntity<>(new SimpleStatusResponse("User name info errored out."),HttpStatus.INTERNAL_SERVER_ERROR);	
		}
	}
}
