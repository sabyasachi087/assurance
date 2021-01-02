package com.sroyc.assurance.core;

import com.auth0.jwt.algorithms.Algorithm;
import com.sroyc.assurance.core.exception.ResetFailureException;

@FunctionalInterface
public interface AssuranceTokenAlgorithmProvider extends Resetable {

	/** Get algorithm to be used for JWT parsing */
	public Algorithm algorithm();

	/**
	 * Resets algorithm. Optional to implement
	 */
	@Override
	default void reset() throws ResetFailureException {
	}

}
