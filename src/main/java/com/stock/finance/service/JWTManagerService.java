package com.stock.finance.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JWTManagerService {

	@Value("${jwt.secret.key:stockServiceApi$ecretKey}")
   private String SECRET_KEY;
   
   public String extractUserName(String token){
     return extractClaim(token, Claims::getSubject);
     }
   
   public Date extractExpiration(String token){
     return extractClaim(token, Claims::getExpiration);
   }
   
   public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
     final Claims claims = extractAllclaims(token);
     return claimsResolver.apply(claims);
   }
   
   private Claims extractAllclaims(String token){
	   //parserClaimsJwt threw below signed claim error 
	   // Actual error message Signed Claims JWSs are not supported.
	   // recommendation was to use parseClaimsJws
      return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
   }
   
   private Boolean isTokenExpired(String token){
     return extractExpiration(token).before(new Date());
   }
   
   public String generateToken(UserDetails userDetails){
      Map<String, Object> claims = new HashMap<>(); // for testing this is empty
      return createToken(claims,userDetails.getUsername());
   }
   
   private String createToken(Map<String,Object> claims, String subject){
      return Jwts.builder().setClaims(claims).setSubject(subject)
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis()+ 1000*60*60))  // the token will expire in 1 hour
                 .setHeaderParam("type", "JWT")   //include this so the jwt.io recommendation of valid signature is achieved
                 .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                 .compact();
   }
   
   public Boolean validateToken(String token, UserDetails userDetails){
      final String userName = extractUserName(token);
      return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
   }
}