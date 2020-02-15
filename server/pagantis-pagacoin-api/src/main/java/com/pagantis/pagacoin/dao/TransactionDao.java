package com.pagantis.pagacoin.dao;

import java.util.Collection;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.model.Wallet;

public interface TransactionDao extends JpaRepository<Transaction, UUID> {
	
	Collection<Transaction> findBySender(Wallet sender);
	
	Collection<Transaction> findByReceiver(Wallet receiver);
	
	Collection<Transaction> findByAmountLessThan(Double max);
	
	Collection<Transaction> findByAmountGreaterThan(Double min);
	
	Collection<Transaction> findByAmountBetween(Double min, Double max);
}
