package com.sroyc.assurance.core.exception;

public class AssuranceRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5880821653928724422L;

	public AssuranceRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssuranceRuntimeException(String message) {
		super(message);
	}

	public AssuranceRuntimeException(Throwable cause) {
		super(cause);
	}

}
