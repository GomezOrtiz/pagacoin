package com.pagantis.pagacoin.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.pagantis.pagacoin.dao.TransactionDao;
import com.pagantis.pagacoin.dao.WalletDao;
import com.pagantis.pagacoin.exception.NotEnoughBalanceException;
import com.pagantis.pagacoin.exception.ResourceNotFoundException;
import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.model.Wallet;
import com.pagantis.pagacoin.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	private TransactionDao transactionDao;
	private WalletDao walletDao;
	
	public TransactionServiceImpl(TransactionDao transactionDao, WalletDao walletDao) {
		this.transactionDao = transactionDao;
		this.walletDao = walletDao;
	}

	@Override
	public Transaction transferAmount(String senderId, String receiverId, Double amount) {
		
		Assert.notNull(senderId, "Debe especificarse la ID de la cartera emisora");
		Assert.notNull(receiverId, "Debe especificarse la ID de la cartera receptora");

		Wallet sender = walletDao.findById(UUID.fromString(senderId)).orElse(null);
		Wallet receiver = walletDao.findById(UUID.fromString(receiverId)).orElse(null);
		
		if(sender == null || receiver == null) {
			throw new ResourceNotFoundException("No se ha podido recuperar la cartera emisora o receptora");
		}
		
		Assert.isTrue(amount != null && amount > 0, "La suma de la transacción no puede ser cero o nula");
		
		if(!isBalanceEnough(sender.getBalance(), amount)) {
			throw new NotEnoughBalanceException("El balance de la cartera emisora es insuficiente para realizar la transacción");
		}
				
		sender.setBalance(sender.getBalance() - amount);
		receiver.setBalance(receiver.getBalance() + amount);
		walletDao.save(sender);
		walletDao.save(receiver);
		
		Transaction transaction = new Transaction(sender, receiver, amount);

		return transactionDao.save(transaction);
	}
	
	private boolean isBalanceEnough(Double senderBalance, Double amount) {
		return (senderBalance - amount) >= 0D;
	}

}
