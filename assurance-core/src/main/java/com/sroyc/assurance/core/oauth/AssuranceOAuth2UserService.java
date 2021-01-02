package com.sroyc.assurance.core.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.sroyc.assurance.core.data.AssuranceUserDetails;

public abstract class AssuranceOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private DefaultOAuth2UserService userService;

	public AssuranceOAuth2UserService() {
		this.userService = new DefaultOAuth2UserService();
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User user = this.userService.loadUser(userRequest);
		return loadUser(user);
	}

	protected abstract AssuranceUserDetails loadUser(OAuth2User user);

}
