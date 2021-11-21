package com.stock.finance.controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
import com.stock.finance.model.TokenRequest;
import com.stock.finance.model.api.AuthenticationResponse;
import com.stock.finance.model.api.SimpleStatusResponse;
import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.user.model.CustomUserDetails;
import com.stock.finance.user.model.Users;
import com.stock.finance.user.service.UserAccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@Operation(description="This end-point is used to provide api key and token when user name and password passed in POST request body.",
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= AuthenticationResponse.class)))})
	@PostMapping("/apikey")
	public ResponseEntity<?> getApiKey(@RequestBody AuthenticationRequest authenticationRequest) throws Exception{

	 final String currentDate = dateFormatter.format(LocalDateTime.now());
	 try{
			//if below authentication we need to catch and throw exception
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(),authenticationRequest.getPassword()));
		} catch (BadCredentialsException e){
			return new ResponseEntity<>(AuthenticationResponse.builder().status("Invalid username or password").build(),HttpStatus.OK);
			//throw new Exception ("Invalid username and password");
		}
		// since now validated we need to get the username and generate JWT
		// we can use the autowired service
        
        String jwt = null;
		final CustomUserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUserName());
		if(userDetails != null) {
			jwt = jwtManagerService.generateTokenWithApi(userDetails.getUsername(),userDetails.getApiKey());
			if(jwt == null || userDetails == null) {
				return new ResponseEntity<>(AuthenticationResponse.builder()
						.status("Username or password doesn't exists.")
						.jwtToken(jwt).build()
						,HttpStatus.OK);
			}
			
			return ResponseEntity.ok(AuthenticationResponse.builder().status("Token generated at "+currentDate)
					                    .jwtToken(jwt).apiKey(userDetails.getApiKey()).build());
		}else {
			log.warn("[getApiKey] userDetails is null for some reason");
			return ResponseEntity.ok(AuthenticationResponse.builder().status("User info not available "+currentDate).jwtToken(jwt));
		}
	}
	
	@Value("${admin.user.info:secretadmin}")
	private String adminUserInfo;
	
	@Tag(name="Signup", description="Use this the sign up for API.")
	@Operation(description="This end-point is used to register or sign-up the user to access the API.",
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= SimpleStatusResponse.class)))})
	@PostMapping("/signup")
	public ResponseEntity<?> signUpUser(@RequestBody AuthenticationRequest userInfo) {
		try {
			if(userInfo != null) {
				//check if the username is already registered
				if(userInfo.getUserName()!=null || !"".equals(userInfo.getUserName())) {
					String userFromDB =userService.getUserNameInfo(userInfo.getUserName());
					if(userFromDB!=null && userFromDB.contains(userInfo.getUserName())) {
						return new ResponseEntity<>(SimpleStatusResponse.builder()
								                       .statusMessage("Hi, "+userFromDB+ " already a registered user, use /apikey endpoint for API key.")
								                       .build(),HttpStatus.OK);
					}
				}
				//else create a new user and save in database
				Users user = new Users();
				user.setUserName(userInfo.getUserName());
				user.setPassword(userInfo.getPassword());
				user.setActive(true);
				
				//generate the API key and set to store in the users table
				user.setApiKey(generateApiKey());
				
				//only for debugging purpose
				if(adminUserInfo.equalsIgnoreCase(user.getUserName())) {
					user.setRoles("ROLE_ADMIN");
				}else {
					user.setRoles("ROLE_USER");
				}
				Users storedUserName = userService.saveUser(user);
				log.info("SignUp endpoint invoked with user info "+user.getUserName());
				return new ResponseEntity<>(SimpleStatusResponse.builder()
						                         .statusMessage("Welcome "+ storedUserName.getUserName() +" !!!, successfully signed up. Use API key to generate token.")
						                         .apiKey(storedUserName.getApiKey())
						                         .build(),HttpStatus.OK); 
			}else {
				log.warn("/singup endpoint invoked, the user info object is null");
				return new ResponseEntity<>(SimpleStatusResponse.builder().statusMessage("SignUp form didn't successfully store user info.")
			  		                                            .build(),HttpStatus.OK);	
			}
		}catch(Exception e) {
			log.error("/singup endpoint invoked, exception occured ",e);
			return new ResponseEntity<>(SimpleStatusResponse.builder().statusMessage("SignUp form didn't successfully store user info.")
					                                        .build(),HttpStatus.INTERNAL_SERVER_ERROR);	
		}
	}
	
	@Operation(description="This end-point is used to provide the token when API key is passed in POST request body.",
			responses = { @ApiResponse(content = @Content(schema=@Schema(implementation= AuthenticationResponse.class)))})
	@PostMapping("/token")
	public ResponseEntity<?> generateTokenWithApiKeyAndUserName(@RequestBody TokenRequest authenticationRequest){

		String jwt = null;
		final String currentDate = dateFormatter.format(LocalDateTime.now());
		try {
			if(authenticationRequest!=null
					&& (authenticationRequest.getUserName()!=null || !"".equals(authenticationRequest.getUserName())) 
					&& (authenticationRequest.getApiKey() !=null || !"".equals(authenticationRequest.getApiKey()))) {
				//check the database if the user name and API exits, then generate token
				Users userFromDB =userService.getUserUsingApiKey(authenticationRequest.getApiKey());

				if(userFromDB == null) {
					throw new Exception("Invalid UserName and API Key, SignUp using /signup endpoint.");
				}
				jwt = jwtManagerService.generateTokenWithApi(userFromDB.getUserName(),userFromDB.getApiKey());
				if(jwt == null ) {
					return new ResponseEntity<>(AuthenticationResponse.builder()
							                      .status("Username or password doesn't exists.")
							                      .jwtToken(jwt)
							                      .build()
							                      ,HttpStatus.OK);
				}
			}
			
			return ResponseEntity.ok(AuthenticationResponse.builder().status("Token generated at "+currentDate).jwtToken(jwt).build());
		}catch(Exception exe) {
			log.error("/token endpoint invoked, exception occured ",exe);
			return new ResponseEntity<>(AuthenticationResponse.builder()
					                        .status("Exception Occured, try again latter. "+exe.getMessage())
					                        .jwtToken(jwt)
					                        .build()
					                        ,HttpStatus.INTERNAL_SERVER_ERROR);	
		}
	}
	
	/**
	 * This method will return an API key and gets stored in the user table. during sign up
	 * @return
	 */
	protected String generateApiKey() {
		MessageDigest salt;
		String digest = UUID.randomUUID().toString();
		try {
			salt = MessageDigest.getInstance("SHA-256");
			salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
			digest = convertBytesToHex(salt.digest());

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			log.error("ApiKey UUID version4 approach errored out, using simple random key. ",e);
			return digest;
		}
		return digest;
	}
	
	private static String convertBytesToHex(byte[] bytes) {
	    return IntStream.range(0, bytes.length)
	    .mapToObj(i -> String.format("%02X", bytes[i]))
	    .collect(Collectors.joining());
	}
}
