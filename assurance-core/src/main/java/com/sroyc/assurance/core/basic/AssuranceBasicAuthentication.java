package com.sroyc.assurance.core.basic;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.sso.AssuranceFailureHandler;
import com.sroyc.assurance.core.sso.AssuranceSuccessHandler;

public class AssuranceBasicAuthentication extends UsernamePasswordAuthenticationFilter
		implements AuthenticationEntryPoint {

	private static final String AUTHENTICATION_FAILURE = "auth_error_msg";
	private AuthenticationManager authManager;
	private AssuranceConfigurationProperty config;
	private AssuranceSuccessHandler successHandler;
	private AssuranceFailureHandler failureHandler;

	public AssuranceBasicAuthentication(AuthenticationManager authManager, AssuranceConfigurationProperty config,
			AssuranceSuccessHandler successHandler, AssuranceFailureHandler failureHandler) {
		this.authManager = authManager;
		this.config = config;
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;
		this.init();
	}

	protected void init() {
		this.setUsernameParameter(this.config.getUsernameAttribute());
		this.setPasswordParameter(this.config.getPasswordAttribute());
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(this.config.getLoginUri(), "POST"));
		this.setAuthenticationSuccessHandler(successHandler);
		this.setAuthenticationFailureHandler(this.failureHandler);
		this.setAuthenticationManager(this.authManager);
		this.afterPropertiesSet();
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		if (StringUtils.hasLength(config.getLoginUri()) && config.isRedirectEnabled()) {
			request.setAttribute(AUTHENTICATION_FAILURE, authException.getMessage());
			response.sendRedirect(config.getLoginUri());
		} else {
			response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
		}
	}

}
