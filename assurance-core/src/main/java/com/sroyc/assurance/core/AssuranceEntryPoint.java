package com.sroyc.assurance.core;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AssuranceEntryPoint implements AuthenticationEntryPoint {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(AssuranceEntryPoint.class);
	private AssuranceConfigurationFactory configFactory;

	@Autowired
	public AssuranceEntryPoint(AssuranceConfigurationFactory configFactory) {
		this.configFactory = configFactory;
	}

	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		this.configFactory.getConfigurations().get(0).entryPoint().commence(request, response, authException);
	}

}
