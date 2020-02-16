package com.pagantis.pagacoin.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.model.Wallet;
import com.pagantis.pagacoin.service.TransactionService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TransactionControllerTest {
	
    @Autowired
    private TestRestTemplate restTemplate;
	
	@MockBean
	private TransactionService service;
	
	private Wallet sender;
	
	private Wallet receiver;
	
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
		
		UUID ownerId = UUID.randomUUID();
        sender = new Wallet.Builder()
        		.withId(ownerId)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
        
        
        UUID receiverId = UUID.randomUUID();
        receiver = new Wallet.Builder()
        		.withId(receiverId)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
	}
	
	@Test
	void shouldTransfer() {
		
		// GIVEN
		Double amount = 50D;
		Transaction transaction = new Transaction(sender, receiver, amount);
		
		Mockito.when(service.transferAmount(sender.getId().toString(), receiver.getId().toString(), amount)).thenReturn(transaction);
		
		MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
		request.add("senderId", sender.getId().toString());
		request.add("receiverId", receiver.getId().toString());
		request.add("amount", String.valueOf(amount));
		
		// WHEN
        ResponseEntity<Transaction> response = restTemplate.postForEntity("/api/transactions/new", request, Transaction.class);
        Transaction responseTransaction = response.getBody();

        // THEN

        assertEquals(HttpStatus.OK, response.getStatusCode(), "El código de estado de la respuesta debería ser el esperado");
        assertTransaction(transaction, responseTransaction);
	}

	private void assertTransaction(Transaction expected, Transaction actual) {
		assertEquals(expected.getId(), actual.getId(), "La id de la transacción debería ser la esperada");
		assertEquals(expected.getSender().getId(), actual.getSender().getId(), "El emisor de la transacción debería ser el esperado");
		assertEquals(expected.getReceiver().getId(), actual.getReceiver().getId(), "El receptor de la transacción debería ser el esperado");
		assertEquals(expected.getAmount(), actual.getAmount(), "La cantidad de la transacción debería ser la esperada");
	}
}
