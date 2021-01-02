package com.sroyc.assurance.web.exception;

public class AssuranceWebRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1835936988228094367L;

	public AssuranceWebRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public AssuranceWebRuntimeException(String message) {
		super(message);
	}

	public AssuranceWebRuntimeException(Throwable cause) {
		super(cause);
	}

}
