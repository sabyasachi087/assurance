package com.sroyc.assurance.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sroyc.noderegistrar.EnableNodeRegistrar;

@EnableNodeRegistrar
@Configuration
@ComponentScan("com.sroyc.assurance")
public class AssuranceAppConfiguration {

}
