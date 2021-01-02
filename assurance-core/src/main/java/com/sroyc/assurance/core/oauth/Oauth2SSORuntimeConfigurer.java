package com.sroyc.assurance.core.oauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;

import com.sroyc.assurance.core.exception.AssuranceConfigurationException;
import com.sroyc.assurance.core.sso.AssuranceSSOConfigurer;

public class Oauth2SSORuntimeConfigurer implements AssuranceSSOConfigurer {

	private static final String DEFAULT_FILTER_REQUEST_PATTERN = "/**/oauth2/**";

	private final ClientRegistrationRepository clientRegistrationRepository;
	private final OAuth2AuthorizedClientService clientService;
	private final OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

	private OAuth2LoginAuthenticationFilter authFilter;
	private OAuth2AuthorizationRequestRedirectFilter redirectFilter;
	private OAuth2LoginAuthenticationProvider authProvider;
	private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;
	private LoginUrlAuthenticationEntryPoint loginEntryPoint;
	private Filter oauthFilter;
	private AuthenticationManager authManager;
	private AuthenticationSuccessHandler successHandler;
	private AuthenticationFailureHandler failureHandler;

	public Oauth2SSORuntimeConfigurer(ClientRegistrationRepository clientRegistrationRepository,
			OAuth2AuthorizedClientService clientService, AuthenticationManager authManager,
			AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler,
			OAuth2UserService<OAuth2UserRequest, OAuth2User> userService) {
		this.clientRegistrationRepository = clientRegistrationRepository;
		this.clientService = clientService;
		this.authManager = authManager;
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;
		this.userService = userService;
	}

	public OAuth2LoginAuthenticationFilter authenticationFilter() {
		if (this.authFilter == null) {
			this.authFilter = new OAuth2LoginAuthenticationFilter(this.clientRegistrationRepository,
					this.clientService);
			this.authFilter.setAuthenticationManager(this.authManager);
			this.authFilter.setAuthenticationSuccessHandler(this.successHandler);
			this.authFilter.setAuthenticationFailureHandler(this.failureHandler);
			this.authFilter.afterPropertiesSet();
		}
		return this.authFilter;
	}

	public OAuth2AuthorizationRequestRedirectFilter authorizationFilter() {
		if (this.redirectFilter == null) {
			this.redirectFilter = new OAuth2AuthorizationRequestRedirectFilter(this.clientRegistrationRepository);
		}
		return this.redirectFilter;
	}

	public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
		if (this.accessTokenResponseClient == null) {
			this.accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
		}
		return this.accessTokenResponseClient;
	}

	public OAuth2LoginAuthenticationProvider authenticationProvider() {
		if (this.authProvider == null) {
			this.authProvider = new OAuth2LoginAuthenticationProvider(this.accessTokenResponseClient(),
					this.userService);
		}
		return this.authProvider;
	}

	public LoginUrlAuthenticationEntryPoint loginEntryPoint() {
		if (this.loginEntryPoint == null) {
			Map<String, String> loginLinks = this.getLoginLinks();
			if (!CollectionUtils.isEmpty(loginLinks)) {
				if (loginLinks.size() == 1) {
					this.loginEntryPoint = new LoginUrlAuthenticationEntryPoint(loginLinks.keySet().iterator().next());
				} else {
					// TODO - Must redirect to client selection page
					throw new AssuranceConfigurationException("Multi Oauth selection page is not implemented");
				}
			}
		}
		return this.loginEntryPoint;
	}

	private Map<String, String> getLoginLinks() {
		Iterable<ClientRegistration> clientRegistrations = (InMemoryClientRegistrationRepository) this.clientRegistrationRepository;
		String authorizationRequestBaseUri = OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
		Map<String, String> loginUrlToClientName = new HashMap<>();
		clientRegistrations.forEach(registration -> loginUrlToClientName.put(
				authorizationRequestBaseUri + "/" + registration.getRegistrationId(), registration.getClientName()));
		return loginUrlToClientName;
	}

	public Filter oauthFilter() {
		if (this.oauthFilter == null) {
			List<SecurityFilterChain> chains = new ArrayList<>();
			chains.add(new DefaultSecurityFilterChain(this.requestMatcher(), this.authenticationFilter(),
					this.authorizationFilter()));
			this.oauthFilter = new FilterChainProxy(chains);
		}
		return this.oauthFilter;
	}

	public RequestMatcher requestMatcher() {
		return new AntPathRequestMatcher(DEFAULT_FILTER_REQUEST_PATTERN);
	}

}
