package com.pagantis.pagacoin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pagantis.pagacoin.exception.NotEnoughBalanceException;
import com.pagantis.pagacoin.exception.ResourceNotFoundException;
import com.pagantis.pagacoin.model.Transaction;
import com.pagantis.pagacoin.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController extends BaseController {
	
	protected Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);
	
	private static final String SENDER_ID = "senderId";
	private static final String RECEIVER_ID = "receiverId";
	private static final String AMOUNT = "amount";
	
	private final TransactionService transactionService;
	
	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	@PostMapping("/new")
	public ResponseEntity<?> postNewTransaction(@RequestParam(SENDER_ID) String senderId, @RequestParam(RECEIVER_ID) String receiverId, @RequestParam(AMOUNT) Double amount) {
		
		try {
			Transaction transaction = transactionService.transferAmount(senderId, receiverId, amount);
			return ResponseEntity.ok(transaction);
		} catch (ResourceNotFoundException rnfe) {
			LOGGER.info("Error al ejecutar la transacción: ".concat(rnfe.getMessage()));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMessage("controllers.transactions.error.not.found"));
		} catch(NotEnoughBalanceException nebe) {
			LOGGER.info("Error al ejecutar la transacción: ".concat(nebe.getMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMessage(nebe.getMessage()));
		} catch (DataAccessException dae) {
			LOGGER.info("Error al ejecutar la transacción: ".concat(dae.getMessage()));
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMessage("generic.error.internal"));
		} catch (IllegalArgumentException iae) {
			LOGGER.info("Error al ejecutar la transacción: ".concat(iae.getMessage()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMessage("controllers.transactions.error.not.valid"));
		}
	}

}
