package com.py.angel.exception;

/**
 * Denotes session is expired.
 */
public class TokenException extends SmartAPIException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenException(String message, String code) {
        super(message, code);
    }
}

