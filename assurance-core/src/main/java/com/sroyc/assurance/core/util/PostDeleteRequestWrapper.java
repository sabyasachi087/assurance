package com.sroyc.assurance.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class PostDeleteRequestWrapper extends HttpServletRequestWrapper {
	protected PostDeleteRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getMethod() {
		return "POST";
	}

	public static final PostDeleteRequestWrapper create(HttpServletRequest request) {
		return new PostDeleteRequestWrapper(request);
	}
}
