package com.pagantis.pagacoin.service.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.pagantis.pagacoin.dao.UserDao;
import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	private final UserDao userDao;
	
	@Autowired
	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public Collection<User> findAll() {
		return userDao.findAll();
	}

	@Override
	public Optional<User> findById(String id) {
		
		Assert.notNull(id, "El identificador de usuario es obligatorio");
		
		return userDao.findById(UUID.fromString(id));
	}
}
