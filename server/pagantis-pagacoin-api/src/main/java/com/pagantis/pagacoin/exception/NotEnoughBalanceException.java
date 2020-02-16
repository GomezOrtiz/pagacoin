package com.pagantis.pagacoin.exception;

public class NotEnoughBalanceException extends RuntimeException {

	private static final long serialVersionUID = 3764266266501397320L;

	public NotEnoughBalanceException() {
	}

	public NotEnoughBalanceException(String message) {
    	super(message, null, false, false);
    }
}