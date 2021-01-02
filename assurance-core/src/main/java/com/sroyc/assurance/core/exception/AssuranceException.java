package com.sroyc.assurance.core.exception;

public class AssuranceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5825130764913755544L;

	public AssuranceException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssuranceException(String message) {
		super(message);
	}

	public AssuranceException(Throwable cause) {
		super(cause);
	}

}
