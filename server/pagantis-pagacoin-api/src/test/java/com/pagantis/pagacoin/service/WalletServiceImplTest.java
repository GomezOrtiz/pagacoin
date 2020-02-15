package com.pagantis.pagacoin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.pagantis.pagacoin.dao.UserDao;
import com.pagantis.pagacoin.dao.WalletDao;
import com.pagantis.pagacoin.exception.ResourceNotFoundException;
import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.model.Wallet;

@SpringBootTest
public class WalletServiceImplTest {
	
	@MockBean
	private WalletDao walletDao;
	
	@MockBean
	private UserDao userDao;
	
	@Autowired
	private WalletService walletService;
	
	private User owner;
	
	private Wallet wallet;
	
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
		
		UUID walletId = UUID.randomUUID();
        wallet = new Wallet.Builder()
        		.withId(walletId)
        		.withOwner(owner)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
	}
	
	@Test
	void shouldFindByOwner() {
		
		// GIVEN
		Mockito.when(userDao.findById(owner.getId())).thenReturn(Optional.of(owner));
		Mockito.when(walletDao.findByOwner(owner)).thenReturn(Arrays.asList(wallet));

		
		// WHEN
		Collection<Wallet> wallets = walletService.findByOwner(owner.getId().toString());
		
		// THEN
		Wallet foundWallet = wallets.iterator().next();
		assertWallet(wallet, foundWallet);
		assertOwner(owner, foundWallet.getOwner());
	}
	
	@Test
	void shouldNotFindByOwner_nullOwnerId () {
		
		assertThrows(IllegalArgumentException.class, () -> {
			walletService.findByOwner(null);
		  });
	}
	
	@Test
	void shouldNotFindByOwner_nonExistentOwner() {
		
		// GIVEN
		Mockito.when(walletDao.findByOwner(owner)).thenReturn(Collections.emptyList());
		
		assertThrows(ResourceNotFoundException.class, () -> {
			walletService.findByOwner(owner.getId().toString());
		  });
	}
	
	@Test
	void shouldFindById() {
		
		// GIVEN
		Mockito.when(walletDao.findById(wallet.getId())).thenReturn(Optional.of(wallet));

		// WHEN
		Wallet foundWallet = walletService.findById(wallet.getId().toString()).orElse(null);
		
		// THEN
		assertWallet(wallet, foundWallet);
		assertOwner(owner, foundWallet.getOwner());
		
		ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
		verify(walletDao).findById(idCaptor.capture());
		assertEquals(idCaptor.getValue(), wallet.getId());
	}
	
	@Test
	void shouldNotFindById_nullId () {
		
		assertThrows(IllegalArgumentException.class, () -> {
			walletService.findById(null);
		  });
	}
	
	@Test
	void shouldNotFindById_notValidUUID () {
		
		assertThrows(IllegalArgumentException.class, () -> {
			walletService.findById("123");
		  });
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
