package com.sroyc.assurance.web.security;

import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sroyc.assurance.core.config.AssuranceWebSecurityAdapter;

public abstract class AssuranceUIEnabledSecurityAdapter extends AssuranceWebSecurityAdapter
		implements WebMvcConfigurer {

	public AssuranceUIEnabledSecurityAdapter(ApplicationContext ctx) {
		super(ctx);
	}

	@Override
	protected void configure(
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry) {
		secure(registry.antMatchers("/css/**").permitAll().antMatchers("/img/**").permitAll().antMatchers("/js/**")
				.permitAll());
	}

	protected abstract void secure(
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry);

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		extendResourceHandler(registry);
		if (!registry.hasMappingForPattern("/static/**")) {
			registry.addResourceHandler("/static/**").addResourceLocations("/static/");
		}
	}

	protected abstract void extendResourceHandler(ResourceHandlerRegistry registry);

}
