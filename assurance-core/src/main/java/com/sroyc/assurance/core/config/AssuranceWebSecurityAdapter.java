package com.sroyc.assurance.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.util.Assert;

import com.sroyc.assurance.core.AssuranceChannelProcessingFilter;
import com.sroyc.assurance.core.AssuranceEntryPoint;
import com.sroyc.assurance.core.AssuranceFilter;
import com.sroyc.assurance.core.AssuranceTokenFilter;
import com.sroyc.assurance.core.sso.AssuranceLogoutHandler;
import com.sroyc.assurance.core.sso.AssuranceProviderManager;

public abstract class AssuranceWebSecurityAdapter extends WebSecurityConfigurerAdapter {

	protected ApplicationContext ctx;
	private AssuranceTokenFilter tokenFilter;
	private AssuranceFilter assuranceFilter;
	private AssuranceChannelProcessingFilter channelProcessingFilter;
	private AssuranceEntryPoint assuranceEntryPoint;
	private AssuranceProviderManager authManager;
	private AssuranceConfigurationProperty config;
	private AssuranceLogoutHandler logoutHandler;

	@Autowired
	public AssuranceWebSecurityAdapter(ApplicationContext ctx) {
		Assert.notNull(ctx, "Application Context cannot be null");
		this.ctx = ctx;
		this.assuranceFilter = ctx.getBean(AssuranceFilter.class);
		this.channelProcessingFilter = ctx.getBean(AssuranceChannelProcessingFilter.class);
		this.assuranceEntryPoint = ctx.getBean(AssuranceEntryPoint.class);
		this.authManager = ctx.getBean(AssuranceProviderManager.class);
		this.tokenFilter = ctx.getBean(AssuranceTokenFilter.class);
		this.config = ctx.getBean(AssuranceConfigurationProperty.class);
		this.logoutHandler = ctx.getBean(AssuranceLogoutHandler.class);
	}

	@Override
	protected final void configure(HttpSecurity http) throws Exception {
		http.httpBasic().authenticationEntryPoint(this.assuranceEntryPoint);
		http.addFilterBefore(this.channelProcessingFilter, ChannelProcessingFilter.class)
				.addFilterAfter(this.assuranceFilter, BasicAuthenticationFilter.class)
				.addFilterBefore(this.assuranceFilter, CsrfFilter.class);
		http.addFilterBefore(tokenFilter, AssuranceFilter.class);
		this.configureLogout(http);
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http
				.authorizeRequests().antMatchers("/basic/**").permitAll().antMatchers("/saml/**").permitAll()
				.antMatchers("/**/oauth2/**").permitAll();
		this.configure(registry);
	}

	protected void configureLogout(HttpSecurity http) throws Exception {
		http.logout().logoutUrl(this.config.getLogoutUri()).deleteCookies(this.config.getJwtTokenKey(), "JSESSIONID")
				.invalidateHttpSession(true).addLogoutHandler(logoutHandler);
	}

	protected abstract void configure(
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry);

	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return this.authManager;
	}

}
