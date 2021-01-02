package com.sroyc.assurance.core;

import org.springframework.security.core.Authentication;

import com.auth0.jwt.interfaces.DecodedJWT;

public interface TokenAdapter {

	/** Converts {@linkplain Authentication} object to Java Web Token */
	public String encode(Authentication auth);

	/**
	 * Parse string java web token. This decoder should be uniform for all type of
	 * token adapters
	 */
	public DecodedJWT decode(String token);

	/** If the adapter supports the given {@linkplain Authentication} object */
	public boolean isSupported(Authentication auth);

}
