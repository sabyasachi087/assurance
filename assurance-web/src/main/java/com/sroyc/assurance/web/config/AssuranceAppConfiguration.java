package com.sroyc.assurance.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.sroyc.noderegistrar.EnableNodeRegistrar;

@EnableNodeRegistrar
@Configuration
@ComponentScan("com.sroyc.assurance")
@EnableMongoRepositories(basePackages = "com.sroyc.assurance.web.mongo")
public class AssuranceAppConfiguration {

}
