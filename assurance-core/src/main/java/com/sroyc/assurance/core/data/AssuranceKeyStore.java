package com.sroyc.assurance.core.data;

import java.io.Serializable;

public class AssuranceKeyStore implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6703878265715127292L;

	private String aksId;
	private String password;
	private String alias;
	private byte[] certificate;
	private CertificateFormat format;

	public String getAksId() {
		return aksId;
	}

	public void setAksId(String aksId) {
		this.aksId = aksId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}

	public CertificateFormat getFormat() {
		return format;
	}

	public void setFormat(CertificateFormat format) {
		this.format = format;
	}

	public enum CertificateFormat {
		PKCS12, JKS;
	}

}
