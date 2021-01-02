package com.sroyc.assurance.core.saml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.util.Assert;

import com.sroyc.assurance.core.Resetable;
import com.sroyc.assurance.core.data.AssuranceKeyStore;
import com.sroyc.assurance.core.data.AssuranceSSOConfig;
import com.sroyc.assurance.core.data.SSOType;
import com.sroyc.assurance.core.exception.AssuranceConfigurationException;
import com.sroyc.assurance.core.exception.AssuranceKeyManagementException;

public class AssuranceKeyManager implements Resetable {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceKeyManager.class);

	private AssuranceSSOConfig config;

	public AssuranceKeyManager(AssuranceSSOConfig config) {
		if (config.getType() == SSOType.SAML) {
			this.config = config;
		} else {
			throw new AssuranceKeyManagementException(
					"Assurance key manager cannot be configured without a valid saml configuration");
		}
	}

	private KeyStore keystore;
	private AssuranceKeyStore aks;
	private JKSKeyManager keyManager;

	private void loadKeystore() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
		if (this.keystore == null) {
			Assert.isTrue(config != null && config.getType() == SSOType.SAML, "No active saml configuration");
			SamlConfiguration samlConfig = (SamlConfiguration) config.getMetadata();
			this.aks = samlConfig.getKeyStore();
			this.keystore = KeyStore.getInstance(this.aks.getFormat().name());
			InputStream is = new ByteArrayInputStream(this.aks.getCertificate());
			Assert.notNull(is, "Unable to load certificate");
			this.keystore.load(is, this.aks.getPassword().toCharArray());
			LOGGER.info("Assurance Key Manager has been loaded");
		}
	}

	@Override
	public void reset() {
		this.keystore = null;
		this.aks = null;
	}

	/**
	 * Generates {@link JKSKeyManager} from private key and certificate
	 * 
	 * @throws AssuranceKeyManagementException
	 */
	public JKSKeyManager getKeyManager() {
		try {
			if (this.keyManager == null) {
				this.loadKeystore();
				Map<String, String> passwords = new HashMap<>();
				passwords.put(this.aks.getAlias(), this.aks.getPassword());
				String defaultKey = this.aks.getAlias();
				if (!this.isAliasKeyExists()) {
					throw new AssuranceConfigurationException(
							"Alias [" + this.aks.getAlias() + "] is unavailable. Check logs for available aliases");
				}
				this.keyManager = new JKSKeyManager(this.keystore, passwords, defaultKey);
			}
		} catch (Exception e) {
			throw new AssuranceKeyManagementException(e);
		}
		return this.keyManager;
	}

	private boolean isAliasKeyExists() throws KeyStoreException {
		Iterator<String> keys = this.keystore.aliases().asIterator();
		while (keys.hasNext()) {
			String key = keys.next();
			LOGGER.info("Key found with alias [{}]", key);
			if (key.equals(this.aks.getAlias())) {
				return true;
			}
		}
		return false;
	}

}
