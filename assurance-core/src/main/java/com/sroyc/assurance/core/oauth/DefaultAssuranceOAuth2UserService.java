package com.sroyc.assurance.core.oauth;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;

import com.sroyc.assurance.core.data.AssuranceCoreConstants;
import com.sroyc.assurance.core.data.AssuranceUserDetails;

public class DefaultAssuranceOAuth2UserService extends AssuranceOAuth2UserService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultAssuranceOAuth2UserService.class);

	public DefaultAssuranceOAuth2UserService() {
		LOGGER.warn(
				"Default user service has been invoked. Must create a custom implementation of [{}] or [{}] for security",
				AssuranceOAuth2UserService.class.getCanonicalName(), OAuth2UserService.class.getCanonicalName());
	}

	@Override
	public AssuranceUserDetails loadUser(Map<String, Object> attributes, Object authenticationUserObject) {
		String username = attributes.get("username") != null ? attributes.get("username").toString()
				: attributes.get("email").toString();
		return new AssuranceUserDetails(username, AssuranceCoreConstants.PASSWORD,
				AuthorityUtils.createAuthorityList(AssuranceCoreConstants.ROLE), attributes);
	}

}
