package com.sroyc.assurance.core.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwt.algorithms.Algorithm;
import com.sroyc.assurance.core.AssuranceTokenAlgorithmProvider;
import com.sroyc.assurance.core.exception.ResetFailureException;

public class SimpleJwtAlgorithm implements AssuranceTokenAlgorithmProvider {

	private static final Logger LOGGER = LogManager.getLogger(SimpleJwtAlgorithm.class);

	private final AtomicReference<Algorithm> reference = new AtomicReference<>(
			Algorithm.HMAC256(UUID.randomUUID().toString()));

	public SimpleJwtAlgorithm() {
		LOGGER.warn("Default implementation of [{}] has been initialized. Must use custom class for security",
				AssuranceTokenAlgorithmProvider.class.getCanonicalName());
	}

	@Override
	public Algorithm algorithm() {
		return reference.get();
	}

	@Override
	/** Refreshes token secret */
	public void reset() throws ResetFailureException {
		reference.set(Algorithm.HMAC256(UUID.randomUUID().toString()));
	}

}
