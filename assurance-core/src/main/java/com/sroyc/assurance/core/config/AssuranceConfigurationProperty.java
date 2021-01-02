package com.sroyc.assurance.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AssuranceConfigurationProperty {

	@Value("${assurance.basic.login.uri:/basic/login}")
	private String loginUri;
	@Value("${assurance.logout.uri:/basic/logout}")
	private String logoutUri;
	@Value("${assurance.basic.username.attribute:username}")
	private String usernameAttribute;
	@Value("${assurance.basic.username.password:password}")
	private String passwordAttribute;

	@Value("${assurance.redirect.enable:true}")
	private boolean isRedirectEnabled;
	@Value("${assurance.auth.success.forwardUri.POST:/basic/success}")
	private String successUri;
	@Value("${assurance.logout.success.forwardUri.POST:/basic/loggedOut}")
	private String successfulLogoutUri;
	@Value("${assurance.auth.failure.forwardUri.POST:/basic/failure}")
	private String failureUri;

	@Value("${assurance.jwt.expiration_in_minutes:30}")
	private Integer jwtExpirationInMinutes;
	@Value("${assurance.jwt.token.prefix:Bearer_}")
	private String jwtTokenPrefix;
	@Value("${assurance.jwt.token.key:Authorization}")
	private String jwtTokenKey;
	@Value("${assurance.cookie.enable:true}")
	private boolean enableCookie;

	/** Activates basic authentication on invoking POST of the URI */
	public String getLoginUri() {
		return loginUri;
	}

	public String getLogoutUri() {
		return logoutUri;
	}

	/**
	 * Determines if the basic entry point will invoke a send redirect or a simple
	 * 401 response on authentication failure
	 */
	public boolean isRedirectEnabled() {
		return isRedirectEnabled;
	}

	public String getUsernameAttribute() {
		return usernameAttribute;
	}

	public String getPasswordAttribute() {
		return passwordAttribute;
	}

	/**
	 * Default handler URI to forward a request on successful authentication. POST
	 * Method Only
	 */
	public String getSuccessUri() {
		return successUri;
	}

	public String getSuccessfulLogoutUri() {
		return successfulLogoutUri;
	}

	/** Default handler URI to forward a request on unsuccessful authentication */
	public String getFailureUri() {
		return failureUri;
	}

	public Integer getJwtExpirationInMinutes() {
		return jwtExpirationInMinutes;
	}

	public String getJwtTokenPrefix() {
		return jwtTokenPrefix;
	}

	public String getJwtTokenKey() {
		return jwtTokenKey;
	}

	/**
	 * If true, success handler will also use cookie apart from header to save token
	 */
	public boolean isEnableCookie() {
		return enableCookie;
	}

}
