package com.sroyc.assurance.web.view.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OauthConfigurationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5466458942023524L;

	private String configId;
	private String configName;
	private boolean active;
	private List<ClientRegistrationVO> clients = new ArrayList<>();
	private String metadata;

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public List<ClientRegistrationVO> getClients() {
		return clients;
	}

	public void setClients(List<ClientRegistrationVO> clients) {
		this.clients = clients;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public static final class ClientRegistrationVO implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1974930565456960447L;

		private String regId;
		private String clientId;
		private String authUri;
		private String userInfoUri;
		private String tokenUri;
		private String jwksUri;

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

		public String getAuthUri() {
			return authUri;
		}

		public void setAuthUri(String authUri) {
			this.authUri = authUri;
		}

		public String getUserInfoUri() {
			return userInfoUri;
		}

		public void setUserInfoUri(String userInfoUri) {
			this.userInfoUri = userInfoUri;
		}

		public String getTokenUri() {
			return tokenUri;
		}

		public void setTokenUri(String tokenUri) {
			this.tokenUri = tokenUri;
		}

		public String getJwksUri() {
			return jwksUri;
		}

		public void setJwksUri(String jwksUri) {
			this.jwksUri = jwksUri;
		}

	}

}
