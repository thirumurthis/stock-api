package com.stock.finance.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.AntPathRequestMatcherProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.stock.finance.service.JWTManagerService;

public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JWTManagerService jwtManager;

	/*
	 * Since JWT filter is executed before the actual url
	 * below is created
	 */
	 @Override
	 protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	    return new AntPathMatcher().match("/stock-app/**", request.getServletPath());
	 }
	 
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse respone, FilterChain chain) throws ServletException, IOException {

		// this is where we example the jwt in header, and validate it.

		//get the jwt from request header Authorization key
		final String authorizationHeader = request.getHeader("Authorization");

		//if the header contains valid jwt, extract the username
		String username = null;
		String jwt = null;

		// additionally validate the expiration, active etc.
		if( authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
			jwt = authorizationHeader.substring(7);
			username = jwtManager.extractUserName(jwt);
		}

		//incase the token didn't had the username, fetch it and validate, set to context

		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			// since the user details is fetched, check if the jwt is valid and not expired
			if (jwtManager.validateToken(jwt,userDetails)){
				// below step will do this automatically, but sine we need to perform this
				// only when the jwt is validated.
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// this executes only when the authentication is null       
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				chain.doFilter(request, respone);
			}
		}
		/*
		String urlPath = request != null ? request.getRequestURI():null;
		String[] splitPath = urlPath != null? urlPath.split("/"):null;
		String currentPath = splitPath!=null?splitPath[splitPath.length-1]:null;
		List<String> allowedPath = Arrays.asList("about","info","authenticate");
		if(currentPath != null && allowedPath.contains(currentPath)){
			SecurityContextHolder.getContext().setAuthentication(null);
			chain.doFilter(request, respone);
		}
		*/
	}


}
