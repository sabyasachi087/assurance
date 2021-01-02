package com.sroyc.assurance.core.sso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sroyc.assurance.core.Resetable;
import com.sroyc.assurance.core.TokenAdapter;
import com.sroyc.assurance.core.TokenAdapterFactory;
import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.exception.AssuranceRuntimeException;
import com.sroyc.assurance.core.exception.ResetFailureException;
import com.sroyc.assurance.core.util.PostDeleteRequestWrapper;

@Component
public class AssuranceSuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements Resetable {

	private AssuranceConfigurationProperty config;
	private List<TokenAdapter> adapters = new ArrayList<>();
	private TokenAdapterFactory adapterFactory;

	@Autowired
	public AssuranceSuccessHandler(AssuranceConfigurationProperty config, TokenAdapterFactory adapterFactory) {
		this.config = config;
		this.adapterFactory = adapterFactory;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		String token = this.config.getJwtTokenPrefix() + createToken(authentication);
		response.addHeader("Access-Control-Expose-Headers", this.config.getJwtTokenKey());
		response.addHeader(this.config.getJwtTokenKey(), token);
		request.setAttribute(this.config.getJwtTokenKey(), token);
		this.saveTokenInCookies(response, token);
		request.getRequestDispatcher(this.config.getSuccessUri()).forward(PostDeleteRequestWrapper.create(request),
				response);
	}

	protected String createToken(Authentication authentication) {
		for (TokenAdapter adapter : this.adapters) {
			if (adapter.isSupported(authentication)) {
				return adapter.encode(authentication);
			}
		}
		throw new AssuranceRuntimeException("No adapter is availble for authentication object type ["
				+ authentication.getClass().getCanonicalName() + "]");
	}

	protected void loadTokenAdapters() {
		this.adapters.addAll(this.adapterFactory.getCustomAdapters());
		this.adapters.addAll(this.adapterFactory.getDefaultAdapters());
	}

	protected void saveTokenInCookies(HttpServletResponse response, String token) {
		if (this.config.isEnableCookie()) {
			String jwtCookie = this.config.getJwtTokenKey() + "=" + token;
			response.setHeader("Set-Cookie", jwtCookie + "; SameSite=strict; Path=/");
		}
	}

	@Override
	public void reset() throws ResetFailureException {
		this.adapterFactory.reset();
		this.adapters.clear();
		this.loadTokenAdapters();
	}

}
