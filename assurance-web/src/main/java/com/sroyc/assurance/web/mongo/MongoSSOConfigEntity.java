package com.sroyc.assurance.web.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sroyc.assurance.core.data.AssuranceSSOConfig;
import com.sroyc.assurance.web.util.UniqueSequenceGenerator;

@Document(collection = "sso_configuration")
public class MongoSSOConfigEntity extends AssuranceSSOConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3440184438940456993L;

	@Id
	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public void autoGenerateId() {
		setId(UniqueSequenceGenerator.CHAR16.next());
	}

}
