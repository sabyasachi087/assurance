package com.sroyc.assurance.core.sso;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.util.PostDeleteRequestWrapper;

@Component
public class AssuranceLogoutHandler implements LogoutHandler {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceLogoutHandler.class);
	private AssuranceConfigurationProperty config;

	@Autowired
	public AssuranceLogoutHandler(AssuranceConfigurationProperty config) {
		super();
		this.config = config;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		SecurityContextHolder.clearContext();
		try {
			request.getRequestDispatcher(this.config.getSuccessfulLogoutUri())
					.forward(PostDeleteRequestWrapper.create(request), response);
		} catch (Exception e) {
			LOGGER.error("Error forwarding request ", e);
		}
	}

}
