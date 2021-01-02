package com.sroyc.assurance.core.oauth;

import java.util.ArrayList;
import java.util.List;

import com.sroyc.assurance.core.data.AssuranceMetadata;

public class Oauth2Configuration implements AssuranceMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5092213357394065845L;

	private List<OauthClientRegister> registrations = new ArrayList<>();

	public List<OauthClientRegister> getRegistrations() {
		return registrations;
	}

	public void setRegistrations(List<OauthClientRegister> registrations) {
		this.registrations = registrations;
	}

}
