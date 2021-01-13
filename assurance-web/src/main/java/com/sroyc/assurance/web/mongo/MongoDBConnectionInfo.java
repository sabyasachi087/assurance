package com.sroyc.assurance.web.mongo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.sroyc.assurance.web.config.DatabaseConnectionInfo;
import com.sroyc.assurance.web.config.DatabaseType;

@Component
@Profile("sroyc.assurance.db.mongo.conn")
public class MongoDBConnectionInfo implements DatabaseConnectionInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5773100466191573997L;

	@Value("${assurance.mongodb.name}")
	private String dbName;
	@Value("${assurance.mongodb.host}")
	private String host;
	@Value("${assurance.mongodb.port}")
	private Integer port;
	@Value("${assurance.mongodb.username}")
	private String username;
	@Value("${assurance.mongodb.password}")
	private String password;

	public String getDbName() {
		return dbName;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public DatabaseType getType() {
		return DatabaseType.MONGO;
	}

}
