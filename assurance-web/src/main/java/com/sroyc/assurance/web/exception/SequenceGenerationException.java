package com.sroyc.assurance.web.exception;

import com.sroyc.assurance.core.exception.AssuranceRuntimeException;

public class SequenceGenerationException extends AssuranceRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7225730132629802992L;

	public SequenceGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SequenceGenerationException(String message) {
		super(message);
	}

	public SequenceGenerationException(Throwable cause) {
		super(cause);
	}

}
