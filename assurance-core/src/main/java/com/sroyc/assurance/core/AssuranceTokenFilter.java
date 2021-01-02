package com.sroyc.assurance.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.data.AssuranceCoreConstants;
import com.sroyc.assurance.core.exception.ResetFailureException;

@Component
public class AssuranceTokenFilter extends OncePerRequestFilter implements Resetable {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceTokenFilter.class);

	private TokenAdapterFactory tokenAdapterFactory;
	private AssuranceConfigurationProperty config;
	private List<TokenAdapter> customAdapters = new ArrayList<>();
	private List<TokenAdapter> defaultAdapters = new ArrayList<>();

	@Autowired
	public AssuranceTokenFilter(TokenAdapterFactory tokenAdapterFactory, AssuranceConfigurationProperty config) {
		this.tokenAdapterFactory = tokenAdapterFactory;
		this.config = config;
		this.loadTokenAdapters();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = loadToken(request);
		DecodedJWT jwt = null;
		if ((jwt = this.extractValidToken(token)) != null) {
			Claim claim = jwt.getClaim(AssuranceCoreConstants.CLAIM_NAME);
			Optional.ofNullable(jwt.getSubject())
					.ifPresent(username -> setUserContext(username, getGrantedAuthorities(claim)));
			request.setAttribute("_jwt_token", token);
		} else {
			SecurityContextHolder.clearContext();
		}
		goToNextFilter(request, response, filterChain);
	}

	protected void setUserContext(String username, List<SimpleGrantedAuthority> authorities) {
		User userDetails = new User(username, "", authorities);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null,
				authorities);
		SecurityContextHolder.getContext().setAuthentication(token);
	}

	protected boolean isTokenValid(String token) {
		return token != null && token.startsWith(this.config.getJwtTokenPrefix());
	}

	protected String getToken(String header) {
		return header.replace(this.config.getJwtTokenPrefix(), "");
	}

	protected DecodedJWT extractValidToken(String header) {
		try {
			if (this.isTokenValid(header)) {
				String token = getToken(header);
				return this.parseToken(getToken(token));
			}
		} catch (Exception e) {
			LOGGER.error("Error validating/decoding token for error >> [{}]", e.getMessage());
		}
		return null;
	}

	protected DecodedJWT parseToken(String token) {
		if (CollectionUtils.isEmpty(this.customAdapters)) {
			// Decode is common for all default adapters
			return this.defaultAdapters.get(0).decode(token);
		} else {
			return this.customAdapters.get(0).decode(token);
		}
	}

	private List<SimpleGrantedAuthority> getGrantedAuthorities(Claim claim) {
		return claim.asList(String.class).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	private void goToNextFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	private String loadToken(HttpServletRequest request) {
		String token = request.getHeader(this.config.getJwtTokenKey());
		if (token == null && request.getCookies() != null) {
			for (Cookie ck : request.getCookies()) {
				if (ck.getName().equals(this.config.getJwtTokenKey())) {
					return ck.getValue();
				}
			}
		}
		return token;
	}

	protected void loadTokenAdapters() {
		this.customAdapters.addAll(this.tokenAdapterFactory.getCustomAdapters());
		this.defaultAdapters.addAll(this.tokenAdapterFactory.getDefaultAdapters());
	}

	@Override
	public void reset() throws ResetFailureException {
		this.tokenAdapterFactory.reset();
		this.customAdapters.clear();
		this.defaultAdapters.clear();
		this.loadTokenAdapters();
	}

}
