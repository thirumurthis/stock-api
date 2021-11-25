package com.stock.finance.client;

import java.util.List;

import com.stock.finance.model.AuthenticationRequest;
import com.stock.finance.model.StockInfoWrapper;
import com.stock.finance.model.TokenRequest;
import com.stock.finance.model.api.About;
import com.stock.finance.model.api.AuthenticationResponse;
import com.stock.finance.model.api.SimpleStatusResponse;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

/*
 * Feign client based on the interface will create the http client.
 */
public interface StockAppFeignClient {

	// below is the feign annotation which will allow to describe the http request
	// used to make the HTTP call
	@RequestLine("GET /stock-app/info")  // NOTE: No domain name is provided here.
	//@Headers({"Content-Type: application/json"})   // this is static, but there is template support to pass dynamic values to header
	                                               // for example to pass in the jwt token
	//Create a method that will create the request.
	// the return type will be used on which the response will de-serialized.
	public Object getAppInfo();
	
	@RequestLine("GET /stock-app/about")
	public About getAboutApp();
	
	@RequestLine("POST /stock-app/signup")
	@Headers({"Content-Type: application/json"})
	public SimpleStatusResponse signUp(AuthenticationRequest request);
	

	@RequestLine("POST /stock-app/apikey")
	@Headers({"Content-Type: application/json"})
	public SimpleStatusResponse getAPIKey(AuthenticationRequest request);

	@RequestLine("POST /stock-app/token")
	@Headers({"Content-Type: application/json"})
	public AuthenticationResponse getToken(TokenRequest request);
	
	@RequestLine("GET /stock/v1/get")
	@Headers({"Content-Type: application/json","Authorization: Bearer {token}"})
	public List<StockInfoWrapper> getStock(@Param("token") String token);
	
}
