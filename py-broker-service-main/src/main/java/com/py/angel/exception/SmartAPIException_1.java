package com.py.angel.exception;

/**
 * This is the base exception class which has a publicly accessible message and
 * code that is received from Angel Connect api.
 */

public class SmartAPIException_1 extends Throwable {

	private static final long serialVersionUID = 1L;
	// variables
	public String message;
	public String code;

	// constructor that sets the message and code
	public SmartAPIException_1(String message, String code) {
		this.message = message;
		this.code = code;
	}

	// constructor that sets the message
	public SmartAPIException_1(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "SmartAPIException [message=" + message + ", code=" + code + "]";
	}

}

