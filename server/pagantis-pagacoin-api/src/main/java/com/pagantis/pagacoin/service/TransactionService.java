package com.pagantis.pagacoin.service;

import com.pagantis.pagacoin.model.Transaction;

public interface TransactionService {
	
	public Transaction transferAmount(String senderId, String receiverId, Double amount);
}
