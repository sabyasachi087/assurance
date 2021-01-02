package com.sroyc.assurance.core.basic;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.sroyc.assurance.core.AssuranceConfigurationRepository;
import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.data.SSOType;
import com.sroyc.assurance.core.exception.ResetFailureException;
import com.sroyc.assurance.core.sso.AssuranceFailureHandler;
import com.sroyc.assurance.core.sso.AssuranceProviderManager;
import com.sroyc.assurance.core.sso.SSOConfiguration;

@Component("basicLoginConfig")
public class BasicLoginConfiguration extends SSOConfiguration {

	private AssuranceFailureHandler failureHandler;
	private AssuranceBasicAuthentication basicAuthFilter;
	private AssuranceConfigurationProperty config;
	private BasicAuthenticationProvider authProvider;

	@Autowired
	protected BasicLoginConfiguration(AssuranceConfigurationRepository configRepo, AssuranceProviderManager provider,
			AssuranceFailureHandler failureHandler, AssuranceConfigurationProperty config, ApplicationContext ctx) {
		super(configRepo, provider, ctx);
		this.failureHandler = failureHandler;
		this.config = config;
	}

	@Override
	public void reset() throws ResetFailureException {
		this.basicAuthFilter = null;
		this.authProvider = null;
		this.init(SSOType.BASIC);
	}

	@Override
	protected void configure() {
		this.basicAuthFilter = new AssuranceBasicAuthentication(this.getAuthManager(), this.config, getSuccessHandler(),
				failureHandler);
		this.authProvider = new BasicAuthenticationProvider(this.context);
	}

	@Override
	public AuthenticationEntryPoint entryPoint() {
		return this.basicAuthFilter;
	}

	@Override
	public Filter filter() {
		return this.basicAuthFilter;
	}

	@Override
	public AuthenticationProvider authProvider() {
		return this.authProvider;
	}

	@Override
	public RequestMatcher matcher() {
		return new AntPathRequestMatcher("/basic/**");
	}

}
