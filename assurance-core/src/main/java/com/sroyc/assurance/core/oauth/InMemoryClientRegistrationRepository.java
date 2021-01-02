package com.sroyc.assurance.core.oauth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.Builder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import com.sroyc.assurance.core.data.AssuranceSSOConfig;

public class InMemoryClientRegistrationRepository
		implements ClientRegistrationRepository, Iterable<ClientRegistration> {
	private static final Logger LOGGER = LogManager.getLogger(InMemoryClientRegistrationRepository.class);

	private final Map<String, ClientRegistration> registrations = new HashMap<>();
	private final AssuranceSSOConfig config;

	public InMemoryClientRegistrationRepository(AssuranceSSOConfig config) {
		Assert.notNull(config, "Configuration cannot be null");
		Assert.notNull(config.getMetadata(), "Configuration metadata cannot be null");
		this.config = config;
		this.initialize();
	}

	protected void initialize() {
		Oauth2Configuration oauthConfig = (Oauth2Configuration) config.getMetadata();
		List<ClientRegistration> clients = oauthConfig.getRegistrations().stream().map(this::transform)
				.collect(Collectors.toList());
		this.registrations.putAll(toUnmodifiableConcurrentMap(clients));
		LOGGER.info("OAuth client registration repository has been initialized/refreshed.");
	}

	@Override
	public Iterator<ClientRegistration> iterator() {
		return this.registrations.values().iterator();
	}

	@Override
	public ClientRegistration findByRegistrationId(String registrationId) {
		Assert.hasText(registrationId, "registrationId cannot be empty");
		return this.registrations.get(registrationId);
	}

	protected ClientRegistration transform(OauthClientRegister client) {
		Builder builder = ClientRegistration.withRegistrationId(client.getRegistrationId());
		builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
		builder.clientId(client.getClientId());
		builder.clientSecret(client.getClientSecret());
		builder.authorizationUri(client.getAuthorizationUri());
		builder.tokenUri(client.getTokenUri());
		builder.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}");
		builder.userInfoUri(client.getUserInfoUri());
		builder.userNameAttributeName("sub");
		builder.jwkSetUri(client.getJwkSetUri());
		return builder.build();
	}

	private static Map<String, ClientRegistration> toUnmodifiableConcurrentMap(List<ClientRegistration> registrations) {
		ConcurrentHashMap<String, ClientRegistration> result = new ConcurrentHashMap<>();
		for (ClientRegistration registration : registrations) {
			if (result.containsKey(registration.getRegistrationId())) {
				throw new IllegalStateException(String.format("Duplicate key %s", registration.getRegistrationId()));
			}
			result.put(registration.getRegistrationId(), registration);
		}
		return Collections.unmodifiableMap(result);
	}

}
