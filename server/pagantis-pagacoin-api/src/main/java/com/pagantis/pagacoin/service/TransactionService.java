package com.pagantis.pagacoin.service;

import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.model.TransactionRequest;

public interface TransactionService {
	
	public Transaction transferAmount(TransactionRequest request);
}
