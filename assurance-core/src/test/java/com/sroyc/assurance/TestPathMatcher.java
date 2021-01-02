package com.sroyc.assurance;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

class TestPathMatcher {

	@Test
	void testPathMatcher() {
		AntPathRequestMatcher matcher = new AntPathRequestMatcher("/**/oauth2/**");
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getContextPath()).thenReturn("/");
		Mockito.when(request.getServletPath()).thenReturn("/login/oauth2/keycloak");
		Assertions.assertTrue(matcher.matches(request));
		Mockito.when(request.getServletPath()).thenReturn("/oauth2/authorization");
		Assertions.assertTrue(matcher.matches(request));
	}

}
