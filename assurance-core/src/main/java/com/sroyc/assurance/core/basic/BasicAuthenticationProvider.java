package com.sroyc.assurance.core.basic;

import java.util.Collections;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.sroyc.assurance.core.sso.AssuranceUserDetailsService;
import com.sroyc.assurance.core.util.AssuranceCommonUtil;

public class BasicAuthenticationProvider implements AuthenticationProvider {

	private AssuranceUserDetailsService userService;

	public BasicAuthenticationProvider(ApplicationContext ctx) {
		AssuranceUserDetailsService uds = AssuranceCommonUtil.getBeanByClass(AssuranceUserDetailsService.class, ctx);
		if (uds == null) {
			uds = new DefaultBasicUserDetailsService(ctx);
		}
		this.userService = uds;
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		if (this.supports(authentication.getClass())) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
			UserDetails user = this.userService.loadUser(Collections.emptyMap(), token);
			if (token.getCredentials() == null || StringUtils.hasText((String) token.getCredentials())
					|| user.getPassword().equals(token.getCredentials())) {
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

}
