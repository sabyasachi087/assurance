package com.sroyc.assurance.core;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;

public abstract class AssuranceTokenAdapter implements TokenAdapter {

	private JWTVerifier verifier;
	protected AssuranceTokenAlgorithmProvider algoProvider;
	protected AssuranceConfigurationProperty config;

	public AssuranceTokenAdapter(AssuranceConfigurationProperty config, AssuranceTokenAlgorithmProvider algoProvider) {
		this.config = config;
		this.algoProvider = algoProvider;
	}

	@Override
	public DecodedJWT decode(String token) {
		if (this.verifier == null) {
			this.verifier = JWT.require(this.algoProvider.algorithm()).acceptLeeway(1).build();
		}
		return this.verifier.verify(token);
	}

	protected Integer getExpirationInMinutes() {
		return this.config.getJwtExpirationInMinutes();
	}

}
