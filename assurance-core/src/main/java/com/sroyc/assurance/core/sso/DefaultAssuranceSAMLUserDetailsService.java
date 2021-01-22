package com.sroyc.assurance.core.sso;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.authority.AuthorityUtils;

import com.sroyc.assurance.core.data.AssuranceCoreConstants;
import com.sroyc.assurance.core.data.AssuranceUserDetails;
import com.sroyc.assurance.core.saml.AssuranceSAMLUserDetailsService;

public class DefaultAssuranceSAMLUserDetailsService extends AssuranceSAMLUserDetailsService {
	private static final Logger LOGGER = LogManager.getLogger(DefaultAssuranceSAMLUserDetailsService.class);

	public DefaultAssuranceSAMLUserDetailsService() {
		LOGGER.warn("Default saml user service is being initialized. Must create custom user service for security");
	}

	@Override
	public AssuranceUserDetails loadUser(Map<String, Object> attributes, Object authenticationUserObject) {
		String username = attributes.get("username") != null ? attributes.get("username").toString()
				: attributes.get("email").toString();
		return new AssuranceUserDetails(username, AssuranceCoreConstants.PASSWORD,
				AuthorityUtils.createAuthorityList(AssuranceCoreConstants.ROLE), attributes);
	}

}
