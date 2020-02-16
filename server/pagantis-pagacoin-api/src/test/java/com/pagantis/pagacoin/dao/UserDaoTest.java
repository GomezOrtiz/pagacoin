package com.pagantis.pagacoin.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
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
public class UserDaoTest {
	
	@Autowired
    private TestEntityManager entityManager;
	
	@Autowired
	private UserDao userDao;
	
	private User user;
	
	private Wallet wallet;
	
	@BeforeEach
	void init() {
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

        UUID walletId = UUID.randomUUID();
        wallet = new Wallet.Builder()
        		.withId(walletId)
        		.withOwner(user)
        		.withCreatedAt(LocalDate.now())
        		.withCreatedBy("ADMIN")
        		.build();
        
        user.setWallets(Collections.singleton(wallet));
        
		entityManager.persist(user);
		entityManager.flush();
	}
	
	@Test
	public void shouldInsertAndFindAll() {
		
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
		
		// WHEN
		User savedUser = userDao.save(newUser);
		Collection<User> result = userDao.findAll();
		
		// THEN
		assertEquals(2, result.size(), "El número de usuarios encontrados debería ser el esperado");
		assertUser(newUser, savedUser);
	}
	
	@Test
	public void shouldFindById() {
		
		// GIVEN
		
		// WHEN
		User foundUser = userDao.findById(user.getId()).orElse(null);
		
		// THEN
		assertUser(user, foundUser);
		assertWallets(wallet, foundUser.getWallets().iterator().next());
		assertNull(foundUser.getUpdatedAt(), "El usuario no debería tener fecha de actualización");
		assertNull(foundUser.getUpdatedBy(), "El usuario no debería tener actualizador");
	}
	
	@Test
	public void shouldNotFindById() {
		
		// GIVEN
        UUID id = UUID.randomUUID();
        while(id.equals(user.getId())) {
        	id = UUID.randomUUID();
        }
        
        //WHEN
        User foundUser = userDao.findById(id).orElse(null);
        
        // THEN
        assertNull(foundUser, "No debería encontrar un usuario con una ID que no existe");
	}
	
	@Test
	public void shouldFindByName() {
		
		// GIVEN
		
		// WHEN
		Collection<User> result = userDao.findByNameContaining("David");
		
		// THEN
		User foundUser = result.iterator().next();
		assertUser(user, foundUser);
		assertWallets(wallet, foundUser.getWallets().iterator().next());
		assertNull(foundUser.getUpdatedAt(), "El usuario no debería tener fecha de actualización");
		assertNull(foundUser.getUpdatedBy(), "El usuario no debería tener actualizador");
	}
	
	@Test
	public void shouldNotFindByName() {
		
		// GIVEN
        
        //WHEN
        Collection<User> result = userDao.findByNameContaining("Pedro");
        
        // THEN
        assertTrue(CollectionUtils.isEmpty(result), "No debería encontrar un usuario con un nombre que no existe");
	}
	
	@Test
	public void shouldFindByEmail() {
		
		// GIVEN
		
		// WHEN
		Collection<User> result = userDao.findByEmailContaining("david.gomez");
		
		// THEN
		User foundUser = result.iterator().next();
		assertUser(user, foundUser);
		assertWallets(wallet, foundUser.getWallets().iterator().next());
		assertNull(foundUser.getUpdatedAt(), "El usuario no debería tener fecha de actualización");
		assertNull(foundUser.getUpdatedBy(), "El usuario no debería tener actualizador");
	}
	
	@Test
	public void shouldNotFindByEmail() {
		
		// GIVEN
        
        //WHEN
		Collection<User> result = userDao.findByEmailContaining("pablo");
        
        // THEN
        assertTrue(CollectionUtils.isEmpty(result), "No debería encontrar un usuario con un email que no existe");
	}
	
	@Test
	public void shouldUpdateUser() {
		
		// GIVEN
		String newEmail = "new.email@gmail.com";
		user.setEmail(newEmail);
		
		// WHEN
		User updatedUser = userDao.save(user);
		
		// THEN
		assertEquals(newEmail, updatedUser.getEmail(), "El correo electrónico debería haberse actualizado");
	}
	
	private void assertUser(User expected, User actual) {
		assertEquals(expected.getId(), actual.getId(), "La id del usuario debería ser la esperada");
		assertEquals(expected.getName(), actual.getName(), "El nombre del usuario debería ser el esperado");
		assertEquals(expected.getSurname(), actual.getSurname(), "El apellido del usuario debería ser el esperado");
		assertEquals(expected.getSecondSurname(), actual.getSecondSurname(), "El segundo apellido del usuario debería ser el esperado");
		assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth(), "La fecha de nacimiento del usuario debería ser la esperada");
		assertEquals(expected.getEmail(), actual.getEmail(), "El correo electrónico del usuario debería ser el esperado");
		assertEquals(expected.getPhone(), actual.getPhone(), "El teléfono electrónico del usuario debería ser el esperado");
		assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "La fecha de creación del usuario debería ser la esperada");
		assertEquals(expected.getCreatedBy(), actual.getCreatedBy(), "El creador del usuario debería ser el esperado");
	}
	
	private void assertWallets(Wallet expected, Wallet actual) {
		assertEquals(expected.getId(), actual.getId(), "La id de la cartera debería ser la esperada");
		assertEquals(expected.getOwner(), actual.getOwner(), "El propietario de la cartera debería ser el esperado");
		assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "La fecha de creación de la cartera debería ser la esperada");
		assertEquals(expected.getCreatedBy(), actual.getCreatedBy(), "El creador de la cartera debería ser el esperado");
		assertEquals(0D, actual.getBalance(), "La cartera debería estar vacía");
	}
}
