package com.pagantis.pagacoin.service;

import java.util.Collection;
import java.util.Optional;

import com.pagantis.pagacoin.model.User;

/**
 * Define un conjunto de operaciones con @User
 * 
 * @author David G.
 *
 */
public interface UserService {

	/**
	 * Recupera todos los @User
	 * 
	 * @return Una colección con todos los @User o vacía si no hay ninguno
	 */
	public Collection<User> findAll();
	
	/**
	 * Recupera un @User a partir de su identificador único
	 * 
	 * @param id
	 * 		El identificador único del usuario
	 * @return Un opcional del usuario recuperado
	 */
	public Optional<User> findById(String id);
}
