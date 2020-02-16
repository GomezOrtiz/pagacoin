package com.pagantis.pagacoin.service.impl;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.pagantis.pagacoin.dao.TransactionDao;
import com.pagantis.pagacoin.dao.WalletDao;
import com.pagantis.pagacoin.exception.NotEnoughBalanceException;
import com.pagantis.pagacoin.exception.ResourceNotFoundException;
import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.model.TransactionRequest;
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
	public Transaction transferAmount(TransactionRequest request) {
		
		Assert.notNull(request.getSenderId(), "Debe especificarse la ID de la cartera emisora");
		Assert.notNull(request.getReceiverId(), "Debe especificarse la ID de la cartera receptora");
		
		Wallet sender = null;
		Wallet receiver = null;
		try {
			sender = walletDao.findById(UUID.fromString(request.getSenderId())).orElse(null);
			receiver = walletDao.findById(UUID.fromString(request.getReceiverId())).orElse(null);
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("La id de la cartera especificada no es válida");
		}

		if(sender == null || receiver == null) {
			throw new ResourceNotFoundException("No se ha podido recuperar la cartera emisora o receptora");
		}
		
		Double amount = request.getAmount();
		
		Assert.isTrue(amount != null && amount > 0, "La cantidad especificada para la transacción no puede ser nula o cero");
		
		if(!isBalanceEnough(sender.getBalance(), amount)) {
			throw new NotEnoughBalanceException("El balance de la cartera emisora es insuficiente para realizar la transacción");
		}
				
		updateAndSaveWallets(sender, receiver, amount);
		
		Transaction transaction = new Transaction(sender, receiver, amount);

		return transactionDao.save(transaction);
	}
	
	private void updateAndSaveWallets(Wallet sender, Wallet receiver, Double amount) {
		sender.setBalance(sender.getBalance() - amount);
		sender.setUpdatedAt(LocalDate.now());
		sender.setUpdatedBy("ADMIN");
		receiver.setBalance(receiver.getBalance() + amount);
		receiver.setUpdatedAt(LocalDate.now());
		receiver.setUpdatedBy("ADMIN");
		walletDao.save(sender);
		walletDao.save(receiver);
	}
	
	private boolean isBalanceEnough(Double senderBalance, Double amount) {
		return (senderBalance - amount) >= 0D;
	}

}
