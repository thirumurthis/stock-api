package com.stock.finance.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    @JsonIgnore  //So this won't be displayed in the bean
    private String password;
    
    @Column(name="ACTIVE",nullable = false,columnDefinition = "boolean default true")
    private boolean active;
    
    @Column(name="ROLES",nullable = true)
    private String roles;
  //  @OneToMany(mappedBy="users")
   // @MappedCollection(keyColumn = "USERNAME", idColumn = "USERNAME")
   // private List<Authorities> authorities;
    
    //api key - for app when signed up
    @Column(name ="API_KEY", nullable = false)
    private String apiKey;
    
    /*
    @Transient
    private List<GrantedAuthority> getGrantedAuthorities(){
    	List<GrantedAuthority> result = this.authorities.stream().map(auth -> new SimpleGrantedAuthority(auth.toString())).collect(Collectors.toList());
    	return result;
    };
    */

}
