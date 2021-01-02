package com.sroyc.assurance.web.view.model;

import java.io.Serializable;

public class SamlConfigurationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6149582457590268542L;

	private String configId;
	private String entityId;
	private String configName;
	private String signAlgo;
	private String metadata;
	private String certificateType;
	private boolean active;

	public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getSignAlgo() {
		return signAlgo;
	}

	public void setSignAlgo(String signAlgo) {
		this.signAlgo = signAlgo;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public String getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
