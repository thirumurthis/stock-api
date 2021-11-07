package com.stock.finance.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stock.finance.user.model.Users;

public interface UserRepository extends JpaRepository<Users,String>{

	Users findByUserName(String userName);
}
