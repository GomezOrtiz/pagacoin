package com.pagantis.pagacoin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.pagantis.pagacoin.dao.TransactionDao;
import com.pagantis.pagacoin.dao.WalletDao;
import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.model.Wallet;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionServiceImplTest {
	
	@MockBean
	private TransactionDao transactionDao;
	
	@MockBean
	private WalletDao walletDao;
	
	@Autowired
	private TransactionService transactionService;
	
	private User owner;
	
	@BeforeEach
	void init () {
		UUID userId = UUID.randomUUID();
		owner = new User.Builder()
				.withId(userId)
				.withName("David")
				.withSurname("Gómez")
				.withSecondSurname("Ortiz")
				.withDateOfBirth(LocalDate.of(1989, 3, 11))
				.withEmail("david.gomez.mail@gmail.com")
				.withPhone("661125956")
				.withCreatedAt(LocalDate.now())
				.withCreatedBy("ADMIN")
				.build();
	}	
	@Test
	void shouldTransferAmount() {
		
		// GIVEN
		
		UUID senderId = UUID.randomUUID();
        Wallet sender = new Wallet.Builder()
        		.withId(senderId)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
        Double senderOriginalBalance = 150D;
        sender.setBalance(senderOriginalBalance);
        
		UUID receiverId = UUID.randomUUID();
        Wallet receiver = new Wallet.Builder()
        		.withId(receiverId)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
        Double receiverOriginalBalance = 50D;
        receiver.setBalance(receiverOriginalBalance);
		
        Double amount = 50D;
        
        Transaction expectedTransaction = new Transaction(sender, receiver, amount);
        
		Mockito.when(walletDao.findById(senderId)).thenReturn(Optional.of(sender));
		Mockito.when(walletDao.findById(receiverId)).thenReturn(Optional.of(receiver));
		Mockito.when(transactionDao.save(any(Transaction.class))).thenReturn(expectedTransaction);
		Mockito.when(walletDao.save(any(Wallet.class))).then(returnsFirstArg());

		// WHEN
		Transaction transaction = transactionService.transferAmount(senderId.toString(), receiverId.toString(), amount);
		
		// THEN
		assertEquals(sender, transaction.getSender(), "El emisor de la transacción debería ser el esperado");
		assertEquals(receiver, transaction.getReceiver(), "El receptor de la transacción debería ser el esperado");
		assertEquals(amount, transaction.getAmount(), "La suma de la transacción debería ser la esperada");
		assertEquals(senderOriginalBalance - amount, sender.getBalance(), "El balance de la cartera emisora después de la transacción debería ser el esperado");
		assertEquals(receiverOriginalBalance + amount, receiver.getBalance(), "El balance de la cartera receptora después de la transacción debería ser el esperado");
	}
}
