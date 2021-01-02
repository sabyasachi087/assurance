package com.sroyc.assurance.web.mongo;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class MongoDBConnectionUrlBuilder {

	public static final String COLON = ":";
	public static final String FORWARD_SLASH = "/";
	public static final String AT_THE_RATE = "@";

	private String host;
	private int port;
	private String dbName;
	private String username;
	private String password;
	private StringBuilder connStringBuilder;

	private MongoDBConnectionUrlBuilder() {
		// Will be invoked from new instance for ease of use
		this.connStringBuilder = new StringBuilder("mongodb://");
	}

	public static final MongoDBConnectionUrlBuilder newInstance() {
		return new MongoDBConnectionUrlBuilder();
	}

	public MongoDBConnectionUrlBuilder host(String host) {
		this.host = host;
		return this;
	}

	public MongoDBConnectionUrlBuilder port(int port) {
		this.port = port;
		return this;
	}

	public MongoDBConnectionUrlBuilder dbName(String dbName) {
		this.dbName = dbName;
		return this;
	}

	public MongoDBConnectionUrlBuilder username(String username) {
		this.username = username;
		return this;
	}

	public MongoDBConnectionUrlBuilder password(String password) {
		this.password = password;
		return this;
	}

	public String build() {
		if (StringUtils.hasLength(this.username) && StringUtils.hasLength(this.password)) {
			this.connStringBuilder.append(this.username).append(COLON).append(this.password).append(AT_THE_RATE);
		}
		Assert.notNull(this.host, "Host cannot be null");
		Assert.notNull(this.port, "Port cannot be null");
		Assert.notNull(this.dbName, "Database name cannot be null");
		this.connStringBuilder.append(this.host).append(COLON).append(this.port).append(FORWARD_SLASH)
				.append(this.dbName);
		return this.connStringBuilder.toString();
	}

}
