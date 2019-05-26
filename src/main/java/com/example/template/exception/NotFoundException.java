package com.example.template.exception;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public NotFoundException(String message) {
		super("cannot find resource: '" + message + "'");
	}

}
