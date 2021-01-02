package com.sroyc.assurance.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

import com.sroyc.assurance.core.config.AssuranceWebSecurityAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityAdapter extends AssuranceWebSecurityAdapter {

	@Autowired
	public WebSecurityAdapter(ApplicationContext ctx) {
		super(ctx);
	}

	@Override
	protected void configure(
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
		registry.antMatchers("/css/**").permitAll().antMatchers("/img/**").permitAll().antMatchers("/js/**").permitAll()
				.anyRequest().authenticated();
	}

}
