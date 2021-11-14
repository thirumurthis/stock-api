package com.stock.finance.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.stock.finance.service.CustomUserDetailsService;
import com.stock.finance.service.JWTManagerService;
import com.stock.finance.user.model.CustomUserDetails;

public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JWTManagerService jwtManager;

	/*
	 * Since JWT filter is executed before the actual url
	 * below is created
	 */
	 @Override
	 protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	    return (new AntPathMatcher().match("/stock-app/**", request.getServletPath()) 
	    		|| new AntPathMatcher().match("/swagger-ui/**", request.getServletPath())
	    		|| new AntPathMatcher().match("/swagger-ui**", request.getServletPath())
	    		|| new AntPathMatcher().match("/stockapp/**", request.getServletPath())
	    		|| new AntPathMatcher().match("/v3/api-docs/**", request.getServletPath()) );
	 }
	 
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

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

		//in case the token didn't had the username, fetch it and validate, set to context

		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);

			// since the user details is fetched, check if the jwt is valid and not expired
			if (jwtManager.validateToken(jwt,userDetails,userDetails.getApiKey())){
				// below step will do this automatically, but sine we need to perform this
				// only when the jwt is validated.
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken 
				                         = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// this executes only when the authentication is null       
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				chain.doFilter(request, response);
			}
		}
		//In case if the security context is with authentication prinicpal, we can perfrom below
		// since in our case we are not persisting any session we don't need it.
		/*else {
			if(username != null && SecurityContextHolder.getContext().getAuthentication() != null
					 && username.equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
				chain.doFilter(request, response);
			};
		}*/
	}
}
