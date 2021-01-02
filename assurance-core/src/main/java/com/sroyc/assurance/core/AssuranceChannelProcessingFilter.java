package com.sroyc.assurance.core;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class AssuranceChannelProcessingFilter extends GenericFilterBean {

	private AssuranceConfigurationFactory configFactory;

	@Autowired
	public AssuranceChannelProcessingFilter(AssuranceConfigurationFactory configFactory) {
		super();
		this.configFactory = configFactory;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (this.configFactory.getChannelProcessingFilter() != null) {
			this.configFactory.getChannelProcessingFilter().doFilter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}
	}
}
