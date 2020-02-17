package com.pagantis.pagacoin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.pagantis.pagacoin.exception.NotEnoughBalanceException;
import com.pagantis.pagacoin.exception.ResourceNotFoundException;
import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.model.TransactionRequest;
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
	private UUID senderId;
	private Double senderOriginalBalance;
	private Wallet sender;
	private Wallet receiver;
	private Double receiverOriginalBalance;
	private UUID receiverId;
	
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
		
		senderId = UUID.randomUUID();
        sender = new Wallet.Builder()
        		.withId(senderId)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
        senderOriginalBalance = 150D;
        sender.setBalance(senderOriginalBalance);
        
		receiverId = UUID.randomUUID();
        receiver = new Wallet.Builder()
        		.withId(receiverId)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
        receiverOriginalBalance = 50D;
        receiver.setBalance(receiverOriginalBalance);
	}	
	
	@Test
	void shouldTransferAmount() {
		
		// GIVEN
		Double amount = 50D;
        Transaction expectedTransaction = new Transaction(sender, receiver, amount);
		TransactionRequest request = new TransactionRequest(senderId.toString(), receiverId.toString(), amount);
        
		Mockito.when(walletDao.findById(senderId)).thenReturn(Optional.of(sender));
		Mockito.when(walletDao.findById(receiverId)).thenReturn(Optional.of(receiver));
		Mockito.when(transactionDao.save(any(Transaction.class))).thenReturn(expectedTransaction);
		Mockito.when(walletDao.save(any(Wallet.class))).then(returnsFirstArg());

		// WHEN
		Transaction transaction = transactionService.transferAmount(request);
		
		// THEN
		assertEquals(sender, transaction.getSender(), "El emisor de la transacción debería ser el esperado");
		assertEquals(receiver, transaction.getReceiver(), "El receptor de la transacción debería ser el esperado");
		assertEquals(amount, transaction.getAmount(), "La suma de la transacción debería ser la esperada");
		assertEquals(senderOriginalBalance - amount, sender.getBalance(), "El balance de la cartera emisora después de la transacción debería ser el esperado");
		assertEquals(receiverOriginalBalance + amount, receiver.getBalance(), "El balance de la cartera receptora después de la transacción debería ser el esperado");
	}
	
	@Test
	void shouldNotTransferAmount_nullSenderId () {
		
		// GIVEN
		TransactionRequest request = new TransactionRequest(null, UUID.randomUUID().toString(), 50D);
		
		// WHEN
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			transactionService.transferAmount(request);
		});
		
		// THEN
		assertEquals("Debe especificarse la ID de la cartera emisora", exception.getMessage(), "El mensaje de error debería ser el esperado");
	}
	
	@Test
	void shouldNotTransferAmount_nullReceiverId () {
		
		// GIVEN
		TransactionRequest request = new TransactionRequest(UUID.randomUUID().toString(), null, 50D);
		
		// WHEN
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			transactionService.transferAmount(request);
		});
		
		// THEN
		assertEquals("Debe especificarse la ID de la cartera receptora", exception.getMessage(), "El mensaje de error debería ser el esperado");
	}
	
	@Test
	void shouldNotTransferAmount_notValidId () {
		
		// GIVEN
		TransactionRequest request = new TransactionRequest(senderId.toString(), receiverId.toString(), 50D);
		
		Mockito.when(walletDao.findById(senderId)).thenThrow(IllegalArgumentException.class);
		
		// WHEN
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			transactionService.transferAmount(request);
		});
		
		// THEN
		assertEquals("La id de la cartera especificada no es válida", exception.getMessage(), "El mensaje de error debería ser el esperado");
	}
	
	@Test
	void shouldNotTransferAmount_nullSenderOrReceiver () {
		
		// GIVEN
		TransactionRequest request = new TransactionRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 50D);
		
		Mockito.when(walletDao.findById(any(UUID.class))).thenReturn(Optional.empty());
		
		// WHEN
		Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
			transactionService.transferAmount(request);
		});
		
		// THEN
		assertEquals("No se ha podido recuperar la cartera emisora o receptora", exception.getMessage(), "El mensaje de error debería ser el esperado");
	}
	
	@Test
	void shouldNotTransferAmount_nullAmount () {
		
		// GIVEN
		TransactionRequest request = new TransactionRequest(receiverId.toString(), senderId.toString(), null);
		
		Mockito.when(walletDao.findById(senderId)).thenReturn(Optional.of(sender));
		Mockito.when(walletDao.findById(receiverId)).thenReturn(Optional.of(receiver));
		
		// WHEN
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			transactionService.transferAmount(request);
		});
		
		// THEN
		assertEquals("La cantidad especificada para la transacción no puede ser nula o cero", exception.getMessage(), "El mensaje de error debería ser el esperado");
	}
	
	@Test
	void shouldNotTransferAmount_zeroAmount () {
		
		// GIVEN
		TransactionRequest request = new TransactionRequest(receiverId.toString(), senderId.toString(), 0D);
		
		Mockito.when(walletDao.findById(senderId)).thenReturn(Optional.of(sender));
		Mockito.when(walletDao.findById(receiverId)).thenReturn(Optional.of(receiver));
		
		// WHEN
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			transactionService.transferAmount(request);
		});
		
		// THEN
		assertEquals("La cantidad especificada para la transacción no puede ser nula o cero", exception.getMessage(), "El mensaje de error debería ser el esperado");
	}
	
	@Test
	void shouldNotTransferAmount_notEnoughBalance () {
		
		// GIVEN
		TransactionRequest request = new TransactionRequest(receiverId.toString(), senderId.toString(), 200D);
		
		Mockito.when(walletDao.findById(senderId)).thenReturn(Optional.of(sender));
		Mockito.when(walletDao.findById(receiverId)).thenReturn(Optional.of(receiver));
		
		// WHEN
		Exception exception = assertThrows(NotEnoughBalanceException.class, () -> {
			transactionService.transferAmount(request);
		});
		
		// THEN
		assertEquals("El balance de la cartera emisora es insuficiente para realizar la transacción", exception.getMessage(), "El mensaje de error debería ser el esperado");
	}
}
