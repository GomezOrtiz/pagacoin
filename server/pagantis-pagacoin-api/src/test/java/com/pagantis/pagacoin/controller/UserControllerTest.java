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
import com.pagantis.pagacoin.service.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {
	
    @Autowired
    private TestRestTemplate restTemplate;
	
	@MockBean
	private UserService service;
	
	private User user;
	
	@BeforeEach
	void init () {
		UUID userId = UUID.randomUUID();
		user = new User.Builder()
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
	void shouldGetAllUsers() {
		
		// GIVEN
		Mockito.when(service.findAll()).thenReturn(Arrays.asList(user));

		// WHEN
        ResponseEntity<User[]> response = restTemplate.getForEntity("/api/users/all", User[].class);
        User[] responseUser = response.getBody();

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode(), "El código de estado de la respuesta debería ser el esperado");
        assertUser(user, responseUser[0]);
	}
	
	@Test
	void shouldGetUserById() {
		
		// GIVEN
		Mockito.when(service.findById(user.getId().toString())).thenReturn(Optional.of(user));

		// WHEN
        ResponseEntity<User> response = restTemplate.getForEntity("/api/users/" + user.getId().toString(), User.class);
        User responseUser = response.getBody();

        // THEN
        assertEquals(HttpStatus.OK, response.getStatusCode(), "El código de estado de la respuesta debería ser el esperado");
        assertUser(user, responseUser);
	}
	
	@Test
	void shouldNotGetUserById_resourceNotFound() {
		
		// GIVEN
		Mockito.when(service.findById(user.getId().toString())).thenThrow(ResourceNotFoundException.class);
		
		// WHEN
        ResponseEntity<?> response = restTemplate.getForEntity("/api/users/" + user.getId().toString(), String.class);

		// THEN
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "El código de estado de la respuesta debería ser el esperado");
        assertEquals("No hemos podido encontrar ningún usuario con el identificador ".concat(user.getId().toString()), response.getBody(), "El cuerpo de la respuesta debería ser el esperado");
	}

	private void assertUser(User expected, User actual) {
		assertEquals(expected.getId(), actual.getId(), "La id del usuario debería ser la esperada");
		assertEquals(expected.getName(), actual.getName(), "El nombre del usuario debería ser el esperado");
		assertEquals(expected.getSurname(), actual.getSurname(), "El apellido del usuario debería ser el esperado");
		assertEquals(expected.getSecondSurname(), actual.getSecondSurname(), "El segundo apellido del usuario debería ser el esperado");
		assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth(), "La fecha de nacimiento del usuario debería ser la esperada");
		assertEquals(expected.getEmail(), actual.getEmail(), "El correo electrónico del usuario debería ser el esperado");
		assertEquals(expected.getPhone(), actual.getPhone(), "El teléfono electrónico del usuario debería ser el esperado");
	}
}
