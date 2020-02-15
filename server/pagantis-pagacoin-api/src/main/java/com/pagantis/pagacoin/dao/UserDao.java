package com.pagantis.pagacoin.dao;

import java.util.Collection;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagantis.pagacoin.model.User;

public interface UserDao extends JpaRepository<User, UUID> {
	
	Collection<User> findByNameContaining(String name);
	
	Collection<User> findByEmailContaining(String email);
}
