package com.pagantis.pagacoin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.pagantis.pagacoin.dao.UserDao;
import com.pagantis.pagacoin.model.User;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceImplTest {
	
	@MockBean
	private UserDao userDao;
	
	@Autowired
	private UserService userService;
	
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
	void shouldFindAll() {
		
		// GIVEN
		Mockito.when(userDao.findAll()).thenReturn(Arrays.asList(user));
		
		// WHEN
		Collection<User> users = userService.findAll();
		
		// THEN
		assertUser(user, users.iterator().next());
	}
	
	@Test
	void shouldFindById() {
		
		// GIVEN
		Mockito.when(userDao.findById(user.getId())).thenReturn(Optional.of(user));

		// WHEN
		User foundUser = userService.findById(user.getId().toString()).orElse(null);
		
		// THEN
		assertUser(user, foundUser);
		
		ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
		verify(userDao).findById(idCaptor.capture());
		assertEquals(idCaptor.getValue(), user.getId());
	}
	
	@Test
	void shouldNotFindById_nullId () {
		
		assertThrows(IllegalArgumentException.class, () -> {
			userService.findById(null);
		  });
	}
	
	@Test
	void shouldNotFindById_notValidUUID () {
		
		assertThrows(IllegalArgumentException.class, () -> {
			userService.findById("123");
		  });
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
