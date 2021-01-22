package com.sroyc.assurance.core.saml;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.impl.XSStringImpl;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import com.sroyc.assurance.core.sso.AssuranceUserDetailsService;

public class AssuranceSAMLUserDetailsService implements SAMLUserDetailsService {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceSAMLUserDetailsService.class);

	private AssuranceUserDetailsService uds;

	public AssuranceSAMLUserDetailsService(AssuranceUserDetailsService uds) {
		this.uds = uds;
	}

	@Override
	public Object loadUserBySAML(SAMLCredential credential) {
		Map<String, Object> attributes = new HashMap<>();
		for (Attribute s : credential.getAttributes()) {
			for (XMLObject val : s.getAttributeValues()) {
				if (val instanceof XSStringImpl) {
					XSStringImpl strVal = (XSStringImpl) val;
					attributes.put(s.getName(), strVal.getValue());
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("{} - {}", s.getName(), strVal.getValue());
				}
			}
		}
		return this.uds.loadUser(attributes, credential);
	}

}
