package com.stock.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import com.stock.finance.service.JWTManagerService;

import java.util.Arrays;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Helper class for creating HTTP Headers before invoking an API with TestRestClient.
 */
@Component
public class JwtHelper {

    @Autowired
    private JWTManagerService jwtProvider;

    public  HttpHeaders tokenWith(String userName,String apiKey){
        HttpHeaders headers = new HttpHeaders();
        
        String token =  jwtProvider.generateTokenWithApi(userName,apiKey);
        headers.setContentType(APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }
}