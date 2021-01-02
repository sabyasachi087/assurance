package com.sroyc.assurance.core.oauth;

import java.util.concurrent.TimeUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientId;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.util.Assert;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * In memory implementation of {@linkplain OAuth2AuthorizedClientService} with
 * {@linkplain Caffeine} cache.
 */
public class AssuranceDefaultCaffeineClientService implements OAuth2AuthorizedClientService {

	private final Cache<OAuth2AuthorizedClientId, OAuth2AuthorizedClient> authorizedClients;

	private final ClientRegistrationRepository clientRegistrationRepository;

	public AssuranceDefaultCaffeineClientService(ClientRegistrationRepository clientRegistrationRepository) {
		Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
		this.clientRegistrationRepository = clientRegistrationRepository;
		this.authorizedClients = Caffeine.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).maximumSize(5000)
				.build();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId,
			String principalName) {
		Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
		Assert.hasText(principalName, "principalName cannot be empty");
		ClientRegistration registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
		if (registration == null) {
			return null;
		}
		return (T) this.authorizedClients
				.getIfPresent(new OAuth2AuthorizedClientId(clientRegistrationId, principalName));
	}

	@Override
	public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
		Assert.notNull(authorizedClient, "authorizedClient cannot be null");
		Assert.notNull(principal, "principal cannot be null");
		this.authorizedClients.put(new OAuth2AuthorizedClientId(
				authorizedClient.getClientRegistration().getRegistrationId(), principal.getName()), authorizedClient);
	}

	@Override
	public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
		Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
		Assert.hasText(principalName, "principalName cannot be empty");
		ClientRegistration registration = this.clientRegistrationRepository.findByRegistrationId(clientRegistrationId);
		if (registration != null) {
			this.authorizedClients.invalidate(new OAuth2AuthorizedClientId(clientRegistrationId, principalName));
		}
	}

}
