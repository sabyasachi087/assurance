package com.sroyc.assurance.core.saml;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.impl.XSStringImpl;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import com.sroyc.assurance.core.data.AssuranceCoreConstants;
import com.sroyc.assurance.core.data.AssuranceUserDetails;

public class DefaultSAMLUserDetailsService implements SAMLUserDetailsService {

	private static final Logger LOGGER = LogManager.getLogger(DefaultSAMLUserDetailsService.class);

	public DefaultSAMLUserDetailsService() {
		LOGGER.warn("Default saml user service is being initialized. Must create custom user service for security");
	}

	@Override
	public Object loadUserBySAML(SAMLCredential credential) {
		Map<String, Object> attributes = new HashMap<>();
		for (Attribute s : credential.getAttributes()) {
			for (XMLObject val : s.getAttributeValues()) {
				if (val instanceof XSStringImpl) {
					XSStringImpl strVal = (XSStringImpl) val;
					attributes.put(s.getName(), strVal.getValue());
					LOGGER.debug("{} - {}", s.getName(), strVal.getValue());
				}
			}
		}
		return new AssuranceUserDetails(credential.getNameID().getValue(), AssuranceCoreConstants.PASSWORD,
				AuthorityUtils.createAuthorityList(AssuranceCoreConstants.ROLE));
	}

}
