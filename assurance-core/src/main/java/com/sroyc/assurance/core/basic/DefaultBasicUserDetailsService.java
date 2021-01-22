package com.sroyc.assurance.core.basic;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sroyc.assurance.core.data.AssuranceCoreConstants;
import com.sroyc.assurance.core.data.AssuranceUserDetails;
import com.sroyc.assurance.core.sso.AssuranceUserDetailsService;

public class DefaultBasicUserDetailsService implements AssuranceUserDetailsService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultBasicUserDetailsService.class);

	private AssuranceUserDetails user;

	public DefaultBasicUserDetailsService(ApplicationContext ctx) {
		String username = ctx.getEnvironment().getProperty("assurance.basic.default.username", "iamadmin");
		String password = ctx.getEnvironment().getProperty("assurance.basic.default.password", "@s$urAnce");
		this.user = new AssuranceUserDetails(username, password,
				AuthorityUtils.createAuthorityList(AssuranceCoreConstants.ROLE));
		LOGGER.warn(
				"Default User Details service has been enabled. Please use a custom implementation of BasicUserDetailsService for security");
	}

	@Override
	public AssuranceUserDetails loadUser(Map<String, Object> attributes, Object authenticationUserObject) {
		if (authenticationUserObject instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authenticationUserObject;
			String username = (String) token.getPrincipal();
			String password = (String) token.getCredentials();
			if (this.user.getUsername().equals(username) && this.user.getPassword().equals(password)) {
				return this.user;
			}
		}
		throw new UsernameNotFoundException("User could not be resolved");
	}

}
