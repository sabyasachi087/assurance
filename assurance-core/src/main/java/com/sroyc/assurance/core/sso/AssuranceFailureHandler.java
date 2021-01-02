package com.sroyc.assurance.core.sso;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.data.AssuranceCoreConstants;
import com.sroyc.assurance.core.util.PostDeleteRequestWrapper;

@Component
public class AssuranceFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private AssuranceConfigurationProperty config;

	@Autowired
	public AssuranceFailureHandler(AssuranceConfigurationProperty config) {
		super();
		this.config = config;
		this.setDefaultFailureUrl(this.config.getFailureUri());
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if (this.isAuthLoginRequest(request) && StringUtils.hasLength(this.config.getFailureUri())) {
			request.setAttribute(AssuranceCoreConstants.AUTH_ERROR_REQUEST_ATTRIBUTE, exception.getMessage());
			request.getRequestDispatcher(this.config.getFailureUri()).forward(PostDeleteRequestWrapper.create(request),
					response);
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}
	}

	private boolean isAuthLoginRequest(HttpServletRequest request) {
		return this.config.getLoginUri().equalsIgnoreCase(request.getRequestURI())
				&& "POST".equals(request.getMethod());
	}

}
