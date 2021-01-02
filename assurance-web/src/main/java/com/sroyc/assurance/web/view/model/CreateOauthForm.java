package com.sroyc.assurance.web.view.model;

import java.io.Serializable;

public class CreateOauthForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -311871708582473450L;

	private String regId;
	private String clientId;
	private String clientSecret;

	private String authUri;
	private String tokenUri;
	private String userInfoUri;
	private String jwksUri;

	private String saveCfg;
	private String addtoConfigId;
	private String configName;

	private String operation = "create";

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getAuthUri() {
		return authUri;
	}

	public void setAuthUri(String authUri) {
		this.authUri = authUri;
	}

	public String getTokenUri() {
		return tokenUri;
	}

	public void setTokenUri(String tokenUri) {
		this.tokenUri = tokenUri;
	}

	public String getUserInfoUri() {
		return userInfoUri;
	}

	public void setUserInfoUri(String userInfoUri) {
		this.userInfoUri = userInfoUri;
	}

	public String getJwksUri() {
		return jwksUri;
	}

	public void setJwksUri(String jwksUri) {
		this.jwksUri = jwksUri;
	}

	public String getAddtoConfigId() {
		return addtoConfigId;
	}

	public void setAddtoConfigId(String addtoConfigId) {
		this.addtoConfigId = addtoConfigId;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getSaveCfg() {
		return saveCfg;
	}

	public void setSaveCfg(String saveCfg) {
		this.saveCfg = saveCfg;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}
