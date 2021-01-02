package com.sroyc.assurance.core.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sroyc.assurance.core.data.AssuranceCoreConstants;
import com.sroyc.assurance.core.data.AssuranceUserDetails;

public class DefaultBasicUserDetailsService implements BasicUserDetailsService {

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
	public UserDetails loadUserByUsername(String username) {
		if (this.user.getUsername().equals(username)) {
			return this.user;
		} else {
			throw new UsernameNotFoundException("User [" + username + "] could not be found");
		}
	}

}
