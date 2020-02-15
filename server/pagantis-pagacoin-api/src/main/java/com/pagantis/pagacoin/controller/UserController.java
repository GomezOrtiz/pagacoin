package com.pagantis.pagacoin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagantis.pagacoin.exception.ResourceNotFoundException;
import com.pagantis.pagacoin.model.User;
import com.pagantis.pagacoin.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController {
	
	protected Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	private final UserService userService;
	
	@Autowired 
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllUsers() {
		try {
			return ResponseEntity.ok(userService.findAll());
		} catch (DataAccessException e) {
			LOGGER.info("Error al recuperar los usuarios: ".concat(e.getMessage()));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMessage("generic.error.internal"));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getOneUser(@PathVariable String id) {
		try {
			User user = userService.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(getMessage("controllers.users.id.error.not.found").concat(id)));
			return ResponseEntity.ok(user);
		} catch (ResourceNotFoundException rnfe) {
			LOGGER.info("Error al recuperar el usuario con ID ".concat(id));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMessage("controllers.users.id.error.not.found").concat(id));
		} catch (DataAccessException dae) {
			LOGGER.info("Error al recuperar el usuario con ID ".concat(id).concat(" : ").concat(dae.getMessage()));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMessage("generic.error.internal"));
		} catch (IllegalArgumentException iae) {
			LOGGER.info("Error al recuperar el usuario con ID ".concat(id).concat(" : ").concat(iae.getMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMessage("controllers.users.id.error.not.valid"));
		}
	}
}
