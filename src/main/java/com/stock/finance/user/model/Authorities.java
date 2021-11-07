package com.stock.finance.user.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.relational.core.mapping.MappedCollection;

@Entity
@Table(name = "AUTHORITIES")
public class Authorities {

	@Id
	@Column(name="USERNAME", nullable = false)
	private String userName;

	@Column(name="AUTHORITY",nullable = true)
	private String authority;

	//@OneToMany
	//private Users users;

	//@MappedCollection(keyColumn = "USERNAME", idColumn = "USERNAME")
    //private List<Users> userList;

}
