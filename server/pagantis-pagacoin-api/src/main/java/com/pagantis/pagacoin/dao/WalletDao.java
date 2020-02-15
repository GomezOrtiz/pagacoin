package com.pagantis.pagacoin.dao;

import java.util.Collection;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.model.Wallet;

public interface WalletDao extends JpaRepository<Wallet, UUID>{
	
	Collection<Wallet> findByOwner(User owner);
}
