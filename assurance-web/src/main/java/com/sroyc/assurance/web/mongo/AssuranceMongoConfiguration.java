package com.sroyc.assurance.web.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@Profile("sroyc.data.mongo")
@EnableMongoRepositories(basePackages = "com.sroyc.assurance.web.mongo")
public class AssuranceMongoConfiguration {

	@Autowired
	private MongoDBConnectionInfo info;

	@Bean
	public MongoClient mongo() {
		String connectionUrl = MongoDBConnectionUrlBuilder.newInstance().dbName(info.getDbName()).host(info.getHost())
				.password(info.getPassword()).username(info.getUsername()).port(info.getPort()).build();
		ConnectionString connectionString = new ConnectionString(connectionUrl);
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyConnectionString(connectionString)
				.build();

		return MongoClients.create(mongoClientSettings);
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongo(), info.getDbName());
	}

}
