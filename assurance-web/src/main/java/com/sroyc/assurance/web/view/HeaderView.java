package com.sroyc.assurance.web.view;

import org.springframework.ui.Model;

import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;

public abstract class HeaderView {

	protected AssuranceConfigurationProperty config;

	public HeaderView(AssuranceConfigurationProperty config) {
		super();
		this.config = config;
	}

	protected void loadHeader(Model model) {
		model.addAttribute("logOutUrl", this.config.getLogoutUri());
	}

	protected void successMessage(Model model, String msg) {
		model.addAttribute("successMsg", msg);
	}

	protected void errorMessage(Model model, String msg) {
		model.addAttribute("errorMsg", msg);
	}

}
