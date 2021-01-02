package com.sroyc.assurance.core.saml;

public enum RSASigningAlgorithm {

	RSA_SHA1("https://www.w3.org/2000/09/xmldsig#rsa-sha1"),
	RSA_SHA256("https://www.w3.org/2001/04/xmldsig-more#rsa-sha256"),
	RSA_SHA512("https://www.w3.org/2001/04/xmldsig-more#rsa-sha512"),
	AES_128("http://www.w3.org/2001/04/xmlenc#aes128-cbc");

	private String algo;

	private RSASigningAlgorithm(String algo) {
		this.algo = algo;
	}

	public String signingAlgorithm() {
		return this.algo;
	}

	public static RSASigningAlgorithm fromValue(String val) {
		for (RSASigningAlgorithm algo : values()) {
			if (algo.name().equals(val) || algo.signingAlgorithm().equals(val)) {
				return algo;
			}
		}
		return null;
	}

}
