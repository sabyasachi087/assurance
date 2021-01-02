package com.sroyc.assurance.core.saml;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;

import com.auth0.jwt.JWT;
import com.sroyc.assurance.core.AssuranceTokenAdapter;
import com.sroyc.assurance.core.AssuranceTokenAlgorithmProvider;
import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.data.AssuranceCoreConstants;

public class DefaultSamlTokenAdapter extends AssuranceTokenAdapter {

	public DefaultSamlTokenAdapter(AssuranceConfigurationProperty config, AssuranceTokenAlgorithmProvider algoProvider) {
		super(config, algoProvider);
	}

	@Override
	public boolean isSupported(Authentication auth) {
		return auth.getClass().equals(ExpiringUsernameAuthenticationToken.class);
	}

	@Override
	public String encode(Authentication auth) {
		ExpiringUsernameAuthenticationToken token = (ExpiringUsernameAuthenticationToken) auth;
		long now = System.currentTimeMillis();
		List<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());
		return JWT.create().withSubject(token.getName()).withClaim(AssuranceCoreConstants.CLAIM_NAME, authorities)
				.withIssuedAt(new Date(now)).withExpiresAt(new Date(now + (getExpirationInMinutes() * 60000l)))
				.sign(this.algoProvider.algorithm());
	}

}
