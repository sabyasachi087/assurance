package com.sroyc.assurance.core.saml;

import org.opensaml.saml2.metadata.provider.AbstractReloadingMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;

public class AssuranceMetadataProvider extends AbstractReloadingMetadataProvider {

	private SamlConfiguration metadata;

	public AssuranceMetadataProvider(SamlConfiguration metadata) {
		this.metadata = metadata;
	}

	@Override
	protected String getMetadataIdentifier() {
		return this.metadata.getEntityId();
	}

	@Override
	protected byte[] fetchMetadata() throws MetadataProviderException {
		return this.metadata.getIdpMetadata().getBytes();
	}

}
