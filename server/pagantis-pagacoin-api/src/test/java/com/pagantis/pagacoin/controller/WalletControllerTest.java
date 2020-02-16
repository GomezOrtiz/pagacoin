package com.pagantis.pagacoin.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
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

import com.pagantis.pagacoin.exception.ResourceNotFoundException;
import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.model.Wallet;
import com.pagantis.pagacoin.service.WalletService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WalletControllerTest {
	
    @Autowired
    private TestRestTemplate restTemplate;
	
	@MockBean
	private WalletService service;
	
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
	void shouldGetWalletsByOwner() {
		
		// GIVEN
		Mockito.when(service.findByOwner(owner.getId().toString())).thenReturn(Arrays.asList(wallet));

		// WHEN
		
        ResponseEntity<Wallet[]> response = restTemplate.getForEntity("/api/wallets/owner/" + owner.getId().toString(), Wallet[].class);
        Wallet[] responseWallet = response.getBody();

        // THEN

        assertEquals(HttpStatus.OK, response.getStatusCode(), "El código de estado de la respuesta debería ser el esperado");
        assertWallet(wallet, responseWallet[0]);
        assertOwner(owner, responseWallet[0].getOwner());
	}
	
	@Test
	void shouldNotGetWalletsByOwner_resourceNotFound() {
		
		// GIVEN
		Mockito.when(service.findByOwner(owner.getId().toString())).thenThrow(ResourceNotFoundException.class);
		
		// WHEN
        ResponseEntity<?> response = restTemplate.getForEntity("/api/wallets/owner/" + owner.getId().toString(), String.class);

		// THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "El código de estado de la respuesta debería ser el esperado");
        assertEquals("No hemos podido encontrar un propietario con el identificador ".concat(owner.getId().toString()), response.getBody(), "El cuerpo de la respuesta debería ser el esperado");
	}
	
	@Test
	void shouldGetWalletById() {
		
		// GIVEN
		Mockito.when(service.findById(wallet.getId().toString())).thenReturn(Optional.of(wallet));

		// WHEN
		
        ResponseEntity<Wallet> response = restTemplate.getForEntity("/api/wallets/" + wallet.getId(), Wallet.class);
        Wallet responseWallet = response.getBody();

        // THEN

        assertEquals(HttpStatus.OK, response.getStatusCode(), "El código de estado de la respuesta debería ser el esperado");
        assertWallet(wallet, responseWallet);
	}

	private void assertWallet(Wallet expected, Wallet actual) {
		assertEquals(expected.getId(), actual.getId(), "La id de la cartera debería ser la esperada");
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
	}
}
