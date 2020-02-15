package com.pagantis.pagacoin.controller;

import java.util.Collection;

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
import com.pagantis.pagacoin.model.Wallet;
import com.pagantis.pagacoin.service.WalletService;

@RestController
@RequestMapping("/api/wallets")
public class WalletController extends BaseController {
	
	protected Logger LOGGER = LoggerFactory.getLogger(WalletController.class);

	private final WalletService walletService;
	
	@Autowired 
	public WalletController(WalletService walletService) {
		this.walletService = walletService;
	}
	
	@GetMapping("/owner/{ownerId}")
	public ResponseEntity<?> getUserWallets(@PathVariable String ownerId) {
		try {
			Collection<Wallet> wallets = walletService.findByOwner(ownerId);
			return ResponseEntity.ok(wallets);
		} catch (ResourceNotFoundException rnfe) {
			LOGGER.info("Error al recuperar las carteras del propietario con ID ".concat(ownerId));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMessage("controllers.wallets.id.error.not.found").concat(ownerId));
		} catch (DataAccessException dae) {
			LOGGER.info("Error al recuperar las carteras del propietario con ID ".concat(ownerId).concat(" : ").concat(dae.getMessage()));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMessage("generic.error.internal"));
		} catch (IllegalArgumentException iae) {
			LOGGER.info("Error al recuperar las carteras del propietario con ID ".concat(ownerId).concat(" : ").concat(iae.getMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMessage("controllers.wallets.id.error.not.valid"));
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getOneWallet(@PathVariable String id) {
		try {
			Wallet wallet = walletService.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException(getMessage("controllers.wallets.id.error.not.found").concat(id)));
			return ResponseEntity.ok(wallet);
		} catch (ResourceNotFoundException rnfe) {
			LOGGER.info("Error al recuperar el usuario con ID ".concat(id).concat(" : ").concat(rnfe.getMessage()));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMessage("controllers.wallets.id.error.not.found").concat(id));
		} catch (DataAccessException dae) {
			LOGGER.info("Error al recuperar el usuario con ID ".concat(id).concat(" : ").concat(dae.getMessage()));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMessage("generic.error.internal"));
		} catch (IllegalArgumentException iae) {
			LOGGER.info("Error al recuperar el usuario con ID ".concat(id).concat(" : ").concat(iae.getMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMessage("controllers.wallets.id.error.not.valid"));
		}
	}

}
