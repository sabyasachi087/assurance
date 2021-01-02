package com.sroyc.assurance.core.exception;

public class AssuranceEntityException extends AssuranceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6067207543417479651L;

	public AssuranceEntityException(String message) {
		super(message);
	}

	public AssuranceEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssuranceEntityException(Throwable cause) {
		super(cause);
	}

}
