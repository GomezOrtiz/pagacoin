package com.pagantis.pagacoin.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.model.Wallet;

@DataJpaTest
public class TransactionDaoTest {
	
	@Autowired
    private TestEntityManager entityManager;
	
	@Autowired
	private TransactionDao transactionDao;
		
	private Wallet sender;
	
	private Wallet receiver;
		
	private User owner;
		
	@BeforeEach
	void init() {
        
        UUID ownerId = UUID.randomUUID();
		owner = new User.Builder()
				.withId(ownerId)
				.withName("David")
				.withSurname("Gómez")
				.withSecondSurname("Ortiz")
				.withDateOfBirth(LocalDate.of(1989, 3, 11))
				.withEmail("david.gomez.mail@gmail.com")
				.withPhone("661125956")
				.withCreatedAt(LocalDate.now())
				.withCreatedBy("ADMIN")
				.build();
		
		entityManager.persist(owner);
		entityManager.flush();
		
		sender = generateWallet();
		receiver = generateWallet();
	}
	
	@Test
	public void shouldInsertAndFindAll() {
		
		// GIVEN
        Transaction transaction1 = new Transaction(sender, receiver, 20.20);
        Transaction transaction2 = new Transaction(sender, receiver, 31.45);
        
		Collection<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction1);
        transactions.add(transaction2);
        
		// WHEN
        Collection<Transaction> savedTransactions  = transactionDao.saveAll(transactions);
		Collection<Transaction> result = transactionDao.findAll();
		
		// THEN
		assertEquals(savedTransactions.size(), result.size(), "El número de transacciones encontradas debería ser el esperado");
		assertTransactions(savedTransactions, result);
	}
	
	@Test
	public void shouldFindBySender() {
		
		// GIVEN
		List<Transaction> transactions = generateTransactions();
        
        Collection<Transaction> expectedTransactions = new ArrayList<Transaction>();
        expectedTransactions.add(transactions.get(0));
        expectedTransactions.add(transactions.get(2));

        
		// WHEN
		Collection<Transaction> foundTransactions = transactionDao.findBySender(sender);

		// THEN
		assertEquals(2, foundTransactions.size(), "El número de transacciones encontradas debería ser el esperado");
		assertTransactions(expectedTransactions, foundTransactions);
	}
	
	@Test
	public void shouldFindByReceiver() {
		
		// GIVEN
		List<Transaction> transactions = generateTransactions();
        
        Collection<Transaction> expectedTransactions = new ArrayList<Transaction>();
        expectedTransactions.add(transactions.get(1));
        expectedTransactions.add(transactions.get(2));
        
		// WHEN
		Collection<Transaction> foundTransactions = transactionDao.findByReceiver(receiver);
		
		// THEN
		assertEquals(2, foundTransactions.size(), "El número de transacciones encontradas debería ser el esperado");
		assertTransactions(expectedTransactions, foundTransactions);
	}
	
	@Test
	public void shouldFindByAmountLessThan() {
		
		// GIVEN
		List<Transaction> transactions = generateTransactions();
        
        Collection<Transaction> expectedTransactions = new ArrayList<Transaction>();
        expectedTransactions.add(transactions.get(1));
        expectedTransactions.add(transactions.get(3));
        
		// WHEN
		Collection<Transaction> foundTransactions = transactionDao.findByAmountLessThan(40D);
		
		// THEN
		assertEquals(2, foundTransactions.size(), "El número de transacciones encontradas debería ser el esperado");
		assertTransactions(expectedTransactions, foundTransactions);
	}
	
	@Test
	public void shouldFindByAmountGreaterThan() {
		
		// GIVEN
		List<Transaction> transactions = generateTransactions();
        
        Collection<Transaction> expectedTransactions = new ArrayList<Transaction>();
        expectedTransactions.add(transactions.get(0));
        expectedTransactions.add(transactions.get(2));
        
		// WHEN
		Collection<Transaction> foundTransactions = transactionDao.findByAmountGreaterThan(40D);
		
		// THEN
		assertEquals(2, foundTransactions.size(), "El número de transacciones encontradas debería ser el esperado");
		assertTransactions(expectedTransactions, foundTransactions);
	}
	
	@Test
	public void shouldFindByAmountBetween() {
		
		// GIVEN
		List<Transaction> transactions = generateTransactions();
        
        Collection<Transaction> expectedTransactions = new ArrayList<Transaction>();
        expectedTransactions.add(transactions.get(1));
        expectedTransactions.add(transactions.get(3));
        
		// WHEN
		Collection<Transaction> foundTransactions = transactionDao.findByAmountBetween(10D, 40D);
		
		// THEN
		assertEquals(2, foundTransactions.size(), "El número de transacciones encontradas debería ser el esperado");
		assertTransactions(expectedTransactions, foundTransactions);
	}
	
	private Wallet generateWallet() {
		UUID id = UUID.randomUUID();
		Wallet wallet = new Wallet.Builder()
				.withId(id)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
		
		entityManager.persist(wallet);
		
		return wallet;
	}
	
	private List<Transaction> generateTransactions() {
		
		Wallet anotherWallet = generateWallet();
	        
		List<Transaction> transactions = new ArrayList<Transaction>();
	        transactions.add(entityManager.persist(new Transaction(sender, anotherWallet, 50.50)));
	        transactions.add(entityManager.persist(new Transaction(anotherWallet , receiver, 20.20)));
	        transactions.add(entityManager.persist(new Transaction(sender, receiver, 60.60)));
	        transactions.add(entityManager.persist(new Transaction(anotherWallet, anotherWallet, 30.30)));
	    
	    return transactions;
	}
	
	private void assertTransactions(Collection<Transaction> expected, Collection<Transaction> actual) {
		Iterator<Transaction> expectedIt = expected.iterator();
		Iterator<Transaction> actualIt = actual.iterator();

		while(actualIt.hasNext()) {
			assertTransaction(expectedIt.next(), actualIt.next());
		}
	}
	
	private void assertTransaction(Transaction expected, Transaction actual) {
		assertEquals(expected.getId(), actual.getId(), "La id de la transacción debería ser la esperada");
		assertWallet(expected.getSender(), actual.getSender());
		assertWallet(expected.getReceiver(), actual.getReceiver());
		assertEquals(expected.getAmount(), actual.getAmount(), "La cantidad de la transacción debería ser la esperada");
	}
	
	private void assertWallet(Wallet expected, Wallet actual) {
		assertEquals(expected.getId(), actual.getId(), "La id de la cartera debería ser la esperada");
		assertEquals(expected.getOwner(), actual.getOwner(), "El propietario de la cartera debería ser el esperado");
		assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "La fecha de creación de la cartera debería ser la esperada");
		assertEquals(expected.getCreatedBy(), actual.getCreatedBy(), "El creador de la cartera debería ser el esperado");
		assertEquals(0D, actual.getBalance(), "La cartera debería estar vacía");
	}
}
