package com.pagantis.pagacoin.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;

import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.model.Wallet;

@DataJpaTest
@ActiveProfiles("test")
public class WalletDaoTest {
	
	@Autowired
    private TestEntityManager entityManager;
	
	@Autowired
	private WalletDao walletDao;
		
	private Wallet wallet;
	
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
		
		UUID walletId = UUID.randomUUID();
        wallet = new Wallet.Builder()
        		.withId(walletId)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
                
		entityManager.persist(wallet);
		entityManager.flush();
	}
	
	@Test
	public void shouldInsertAndFindAll() {
		
		// GIVEN
		UUID id = UUID.randomUUID();
        Wallet newWallet = new Wallet.Builder()
        		.withId(id)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
		
		// WHEN
		Wallet savedWallet  = walletDao.save(newWallet);
		Collection<Wallet> result = walletDao.findAll();
		
		// THEN
		assertEquals(2, result.size(), "El número de carteras encontradas debería ser el esperado");
		assertWallet(newWallet, savedWallet);
	}
	
	@Test
	public void shouldFindById() {
		
		// GIVEN
		
		// WHEN
		Wallet foundWallet = walletDao.findById(wallet.getId()).orElse(null);
		
		// THEN
		assertWallet(wallet, foundWallet);
		assertOwner(owner, foundWallet.getOwner());
		assertNull(foundWallet.getUpdatedAt(), "La cartera no debería tener fecha de actualización");
		assertNull(foundWallet.getUpdatedBy(), "La cartera no debería tener actualizador");
	}
	
	@Test
	public void shouldNotFindById() {
		
		// GIVEN
        UUID id = UUID.randomUUID();
        while(id.equals(wallet.getId())) {
        	id = UUID.randomUUID();
        }
        
        //WHEN
        Wallet foundWallet = walletDao.findById(id).orElse(null);
        
        // THEN
        assertNull(foundWallet, "No debería encontrar una cartera con una ID que no existe");
	}
	
	@Test
	public void shouldFindByOwner() {
		
		// GIVEN
		
		// WHEN
		Collection<Wallet> result = walletDao.findByOwner(owner);
		
		// THEN
		Wallet foundWallet = result.iterator().next();
		assertWallet(wallet, foundWallet);
		assertOwner(owner, foundWallet.getOwner());
		assertNull(foundWallet.getUpdatedAt(), "La cartera no debería tener fecha de actualización");
		assertNull(foundWallet.getUpdatedBy(), "La cartera no debería tener actualizador");
	}
	
	@Test
	public void shouldNotFindByOwner() {
		
		// GIVEN
        UUID id = UUID.randomUUID();
		User newUser = new User.Builder()
				.withId(id)
				.withName("Carlos")
				.withSurname("Pérez")
				.withSecondSurname("Rodríguez")
				.withDateOfBirth(LocalDate.of(1975, 5, 21))
				.withEmail("carlos.perez@gmail.com")
				.withPhone("677558822")
				.withCreatedAt(LocalDate.now())
				.withCreatedBy("ADMIN")
				.build();
		
        //WHEN
		Collection<Wallet> result = walletDao.findByOwner(newUser);
        
        // THEN
        assertTrue(CollectionUtils.isEmpty(result), "No debería encontrar una cartera de un usuario sin carteras");
	}
	
	@Test
	public void shouldUpdateWallet() {
		
		// GIVEN
		Double newBalance = 50D;
		wallet.setBalance(newBalance);
		
		// WHEN
		Wallet updatedWallet = walletDao.save(wallet);
		
		// THEN
		assertEquals(newBalance, updatedWallet.getBalance(), "El saldo debería haberse actualizado");
	}
	
	private void assertWallet(Wallet expected, Wallet actual) {
		assertEquals(expected.getId(), actual.getId(), "La id de la cartera debería ser la esperada");
		assertEquals(expected.getOwner(), actual.getOwner(), "El propietario de la cartera debería ser el esperado");
		assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "La fecha de creación de la cartera debería ser la esperada");
		assertEquals(expected.getCreatedBy(), actual.getCreatedBy(), "El creador de la cartera debería ser el esperado");
		assertEquals(0D, actual.getBalance(), "La cartera debería estar vacía");
	}
	
	private void assertOwner(User expected, User actual) {
		assertEquals(expected.getId(), actual.getId(), "La id del propietario debería ser la esperada");
		assertEquals(expected.getName(), actual.getName(), "El nombre del propietario debería ser el esperado");
		assertEquals(expected.getSurname(), actual.getSurname(), "El apellido del propietario debería ser el esperado");
		assertEquals(expected.getSecondSurname(), actual.getSecondSurname(), "El segundo apellido del propietario debería ser el esperado");
		assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth(), "La fecha de nacimiento del propietario debería ser la esperada");
		assertEquals(expected.getEmail(), actual.getEmail(), "El correo electrónico del propietario debería ser el esperado");
		assertEquals(expected.getPhone(), actual.getPhone(), "El teléfono electrónico del propietario debería ser el esperado");
		assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "La fecha de creación del propietario debería ser la esperada");
		assertEquals(expected.getCreatedBy(), actual.getCreatedBy(), "El creador del propietario debería ser el esperado");
	}
}
