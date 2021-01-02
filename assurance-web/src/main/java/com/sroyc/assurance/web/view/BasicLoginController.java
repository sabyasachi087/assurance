package com.sroyc.assurance.web.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/basic")
public class BasicLoginController {

	private static final String LOGIN_PAGE = "asr/login.html";
	private static final String LOGGEDOUT_PAGE = "asr/logout.html";

	@PostMapping("/failure")
	public String failure(HttpServletRequest request, HttpServletResponse response) {
		return LOGIN_PAGE;
	}

	@GetMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response) {
		return LOGIN_PAGE;
	}

	@PostMapping("/loggedOut")
	public String loggedOut(HttpServletRequest request, HttpServletResponse response) {
		return LOGGEDOUT_PAGE;
	}

	@PostMapping("/success")
	public String success(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("redirectTo", "/assurance/home");
		return LOGIN_PAGE;
	}

}
