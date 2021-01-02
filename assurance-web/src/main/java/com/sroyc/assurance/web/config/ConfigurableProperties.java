package com.sroyc.assurance.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigurableProperties {

	@Autowired
	private DatabaseConnectionInfo connectionInfo;

	public DatabaseConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

}
