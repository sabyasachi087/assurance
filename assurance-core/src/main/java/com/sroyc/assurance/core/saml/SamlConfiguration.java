package com.sroyc.assurance.core.saml;

import com.sroyc.assurance.core.data.AssuranceKeyStore;
import com.sroyc.assurance.core.data.AssuranceMetadata;

public class SamlConfiguration implements AssuranceMetadata {

	/**
	 * 
	 */
	private static final long serialVersionUID = -306217958489465966L;

	private String entityId;
	private RSASigningAlgorithm signingAlogrithm;
	private String idpMetadata;
	private AssuranceKeyStore keyStore;

	public String getIdpMetadata() {
		return idpMetadata;
	}

	public void setIdpMetadata(String idpMetadata) {
		this.idpMetadata = idpMetadata;
	}

	public AssuranceKeyStore getKeyStore() {
		return keyStore;
	}

	public void setKeyStore(AssuranceKeyStore keyStore) {
		this.keyStore = keyStore;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public RSASigningAlgorithm getSigningAlogrithm() {
		return signingAlogrithm;
	}

	public void setSigningAlogrithm(RSASigningAlgorithm signingAlogrithm) {
		this.signingAlogrithm = signingAlogrithm;
	}

}
