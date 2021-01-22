package com.sroyc.assurance.core.sso;

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.sroyc.assurance.core.data.AssuranceUserDetails;

/** Contract for creating User object from single sign on attributes */
@FunctionalInterface
public interface AssuranceUserDetailsService {

	/**
	 * Create {@linkplain AssuranceUserDetails} from the single sign on attributes.
	 * AuthenticationUserObject is optional field and can be a
	 * {@linkplain SAMLCredential} / {@linkplain OAuth2User} or any other
	 * proprietary user objects. The Map attributes must contain all the user
	 * information retrieved from SSO server. <br/>
	 * In case of BasicAuthentication, <i>authenticationUserObject</i> will be an
	 * instance of {@linkplain UsernamePasswordAuthenticationToken} and
	 * <i>attributes</i> will be an empty map
	 */
	AssuranceUserDetails loadUser(Map<String, Object> attributes, @Nullable Object authenticationUserObject);

}
