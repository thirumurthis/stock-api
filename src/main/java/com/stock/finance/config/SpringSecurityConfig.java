package com.stock.finance.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.stock.finance.filter.JwtRequestFilter;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	JwtRequestFilter jwtRequestFilter;

	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic()
		     .and()
            .authorizeRequests()
			.antMatchers("/stock-app/**","/swagger-ui/**","/stockapp-openapi/**").permitAll()
			.antMatchers("/h2-console/**").hasRole("ADMIN")
			.antMatchers("/**").authenticated()
			.anyRequest().authenticated()
			.and()
			//.formLogin().loginPage("/login").permitAll()
			.formLogin().permitAll()
			.and()
			.logout().permitAll();
		http.exceptionHandling().accessDeniedPage("/403");
		http.csrf().disable();
		//http.csrf().ignoringAntMatchers("/h2-console/**","/stock/v1/add","/v1/authenticate");
		http.headers().frameOptions().disable();
		
		http.sessionManagement()  //adding session management, saying spring not to create session.
		    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		   // this stateless makes spring not to create session
		   // add the new filter
		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}

	//*
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				.withUser("user").password(passwordEncoder().encode("user")).roles("USER")
				.and()
				.withUser("admin").password(passwordEncoder().encode("admin")).roles("ADMIN");
	}
	//*/
	 @Bean
	  PasswordEncoder passwordEncoder(){
	    return new BCryptPasswordEncoder(); // use of NoOpPasswordEncoder is strictly for development purpose only
	  }
	 
	 //in order to make the Authenticate restcontroller end point to work we need to create the below bean
	 // this needs to be created for spring 2+ onwards
	 @Override
	 @Bean
	 public AuthenticationManager authenticationManagerBean() throws Exception{
		 return super.authenticationManagerBean();
	 }
	 
}