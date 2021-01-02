package com.sroyc.assurance.core.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class BasicAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOGGER = LogManager.getLogger(BasicAuthenticationProvider.class);

	private BasicUserDetailsService userService;

	public BasicAuthenticationProvider(ApplicationContext ctx) {
		if ((this.userService = this.getUserService(ctx)) == null) {
			this.userService = new DefaultBasicUserDetailsService(ctx);
			LOGGER.warn("Falling back to default implementation of BasicUserDetailsService");
		}
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		if (this.supports(authentication.getClass())) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
			UserDetails user = this.userService.loadUserByUsername((String) token.getPrincipal());
			if (user.getPassword().equals(token.getCredentials())) {
				return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),
						user.getAuthorities());
			} else {
				throw new BadCredentialsException("Username and password mismatched");
			}
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.equals(authentication);
	}

	protected BasicUserDetailsService getUserService(ApplicationContext ctx) {
		try {
			return ctx.getBean(BasicUserDetailsService.class);
		} catch (Exception ex) {
			LOGGER.error("Unable to retrieve BasicUserDetailsService with error {}", ex.getMessage());
		}
		return null;
	}

}
