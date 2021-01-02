package com.sroyc.assurance.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssuranceFilter implements Filter {

	private AssuranceConfigurationFactory configFactory;

	@Autowired
	public AssuranceFilter(AssuranceConfigurationFactory configFactory) {
		this.configFactory = configFactory;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		this.configFactory.filter().doFilter(request, response, chain);
	}

}
