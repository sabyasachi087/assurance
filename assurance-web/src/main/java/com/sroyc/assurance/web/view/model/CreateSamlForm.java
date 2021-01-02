package com.sroyc.assurance.web.view.model;

import java.io.Serializable;

public class CreateSamlForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4753983076599965956L;
	private String configName;
	private String entityId;
	private String signAlgo = null;
	private String idpMetadata = null;
	private String certificateType;
	private String certAlias;
	private String certPwd;

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public String getSignAlgo() {
		return signAlgo;
	}

	public void setSignAlgo(String signAlgo) {
		this.signAlgo = signAlgo;
	}

	public String getIdpMetadata() {
		return idpMetadata;
	}

	public void setIdpMetadata(String idpMetadata) {
		this.idpMetadata = idpMetadata;
	}

	public String getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}

	public String getCertAlias() {
		return certAlias;
	}

	public void setCertAlias(String certAlias) {
		this.certAlias = certAlias;
	}

	public String getCertPwd() {
		return certPwd;
	}

	public void setCertPwd(String certPwd) {
		this.certPwd = certPwd;
	}

	@Override
	public String toString() {
		return "CreateSamlForm [configName=" + configName + ", entityId=" + entityId + ", signAlgo=" + signAlgo
				+ ", idpMetadata=" + idpMetadata + ", certificateType=" + certificateType + ", certAlias=" + certAlias
				+ "]";
	}

}
