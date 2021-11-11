package com.team6.apps.search.security;

public class JwtException extends Exception {

	private static final long serialVersionUID = 1L;

	public JwtException(String message) {
		super(message);
	}

	public JwtException() {
		super();
	}

	public JwtException(String message, Throwable cause) {
		super(message, cause);
	}

	public JwtException(Throwable cause) {
		super(cause);
	}
}
