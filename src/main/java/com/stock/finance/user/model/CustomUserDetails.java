package com.stock.finance.user.model;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

//Currently not used
@Deprecated
public class CustomUserDetails implements UserDetails{

	private static final long serialVersionUID = 1L;
	private final Set<GrantedAuthority> authorities;
	private final String username,password;
	private final boolean active;
	// we will initalize a construtor to get the values

	// this is a simple domain 
	public CustomUserDetails (String username, String password, boolean active, String ... authorities){
		this.username=username;
		this.password=password;
		this.active = active;
		this.authorities = Stream.of(authorities)
				.map(SimpleGrantedAuthority::new)  // this is shortcut of a -> new SimpleGrantedAuthority()
				.collect(Collectors.toSet()); // the passed in value is converted as set here
	}
	@Override 
	public Collection<? extends GrantedAuthority> getAuthorities(){
		// since we have set these values in the constructor we now send these
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}
	@Override
	public boolean isAccountNonExpired() {
		return this.active;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		return this.active;
	}
}