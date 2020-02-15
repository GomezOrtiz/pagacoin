package com.pagantis.pagacoin.service;

import java.util.Collection;
import java.util.Optional;

import com.pagantis.pagacoin.model.Wallet;

public interface WalletService {
	
	Collection<Wallet> findByOwner(String ownerId);
	
	Optional<Wallet> findById(String id);
}
