package com.stock.finance.user.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="USERS")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Users {

	@Id
	@Column(name="USERNAME", nullable = false,unique = true)
	private String userName;
	
    @Column(name="EMAIL", nullable = true)
    private String email;
    
    @Column(name="PASSWORD", nullable = false)
    private String password;
    
    @Column(name="ACTIVE",nullable = false,columnDefinition = "boolean default true")
    private boolean active;
    
    @Column(name="ROLES",nullable = true)
    private String roles;
  //  @OneToMany(mappedBy="users")
   // @MappedCollection(keyColumn = "USERNAME", idColumn = "USERNAME")
   // private List<Authorities> authorities;
    
    /*
    @Transient
    private List<GrantedAuthority> getGrantedAuthorities(){
    	List<GrantedAuthority> result = this.authorities.stream().map(auth -> new SimpleGrantedAuthority(auth.toString())).collect(Collectors.toList());
    	return result;
    };
    */

}
