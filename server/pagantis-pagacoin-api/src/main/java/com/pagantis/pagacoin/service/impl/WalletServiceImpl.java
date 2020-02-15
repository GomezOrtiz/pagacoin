package com.pagantis.pagacoin.service.impl;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.pagantis.pagacoin.dao.UserDao;
import com.pagantis.pagacoin.dao.WalletDao;
import com.pagantis.pagacoin.exception.ResourceNotFoundException;
import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.model.Wallet;
import com.pagantis.pagacoin.service.WalletService;

@Service
public class WalletServiceImpl implements WalletService {
	
	private WalletDao walletDao; 
	private UserDao userDao;
	
	@Autowired
	public WalletServiceImpl(WalletDao walletDao, UserDao userDao) {
		this.walletDao = walletDao;
		this.userDao = userDao;
	}

	@Override
	public Collection<Wallet> findByOwner(String ownerId) {
		
		Assert.notNull(ownerId, "La ID del propietario de la cartera es obligatoria");
		
		User owner = userDao.findById(UUID.fromString(ownerId)).orElse(null);
		
		if(owner == null) {
			throw new ResourceNotFoundException("No hemos podido recuperar el propietario con ID ".concat(ownerId));
		}
		
		return walletDao.findByOwner(owner);
	}

	@Override
	public Optional<Wallet> findById(String id) {
		
		Assert.notNull(id, "El identificador de la cartera es obligatorio");
		
		return walletDao.findById(UUID.fromString(id));	
	}
	
	

}
