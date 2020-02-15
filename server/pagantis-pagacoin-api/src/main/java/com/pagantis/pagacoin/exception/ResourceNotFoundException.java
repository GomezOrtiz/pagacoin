package com.pagantis.pagacoin.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6920817992730703708L;

	public ResourceNotFoundException() {
	}

	public ResourceNotFoundException(String message) {
    	super(message, null, false, false);
    }
}