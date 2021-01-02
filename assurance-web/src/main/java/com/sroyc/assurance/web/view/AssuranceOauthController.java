package com.sroyc.assurance.web.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.exception.AssuranceEntityException;
import com.sroyc.assurance.web.service.AssuranceWebService;
import com.sroyc.assurance.web.view.model.CreateOauthForm;
import com.sroyc.assurance.web.view.model.IdName;
import com.sroyc.assurance.web.view.model.OauthConfigurationVO;

@Controller
@RequestMapping("/assurance/oauth")
public class AssuranceOauthController extends HeaderView {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceOauthController.class);
	private static final String FORM_NAME = "createOauthForm";
	private static final String PAGE_URL = "asr/oauth/oauth.html";
	private static final String[] SAVE_CFG_OPTIONS = { "", "New", "Existing" };
	private static final String[] OPERATIONS_OPTIONS = { "create", "select_onchange" };

	private AssuranceWebService service;

	@Autowired
	public AssuranceOauthController(AssuranceConfigurationProperty config, AssuranceWebService service) {
		super(config);
		this.service = service;
	}

	@GetMapping
	public String getCreateConfigPage(Model model) throws AssuranceEntityException {
		this.loadBaseAttributes(model, new CreateOauthForm());
		return PAGE_URL;
	}

	@PostMapping
	public String createConfig(@ModelAttribute CreateOauthForm form, Model model) throws AssuranceEntityException {
		if (OPERATIONS_OPTIONS[0].equalsIgnoreCase(form.getOperation())) {
			if (this.validate(form)) {
				this.service.saveOauthConfig(form);
				successMessage(model, "Configuration has been saved successfully");
				form = new CreateOauthForm();
			} else {
				errorMessage(model, "All fields are mandatory");
			}
		}
		this.loadBaseAttributes(model, form);
		return PAGE_URL;
	}

	@PostMapping("/delete")
	public String deleteConfig(Model model, HttpServletRequest request) throws AssuranceEntityException {
		String configIdToBeDeleted = request.getParameter("configIdToBeDeleted");
		this.service.deleteConfig(configIdToBeDeleted);
		model.addAttribute("successMsg",
				"Configuration Id [" + configIdToBeDeleted + "] has been removed successfully");
		this.loadBaseAttributes(model, new CreateOauthForm());
		return PAGE_URL;
	}

	private List<IdName> getExistingConfigs(List<OauthConfigurationVO> oauthConfigs) {
		List<IdName> cfgs = new ArrayList<>();
		cfgs.add(new IdName("", "--Choose--"));
		cfgs.addAll(oauthConfigs.stream().map(cfg -> new IdName(cfg.getConfigId(), cfg.getConfigName()))
				.collect(Collectors.toList()));
		return cfgs;
	}

	private void loadBaseAttributes(Model model, CreateOauthForm form) throws AssuranceEntityException {
		form.setOperation(OPERATIONS_OPTIONS[0]);
		model.addAttribute(FORM_NAME, form);
		model.addAttribute("saveCfgOptions", Arrays.asList(SAVE_CFG_OPTIONS));
		this.loadHeader(model);
		List<OauthConfigurationVO> oauthConfigs = this.service.getAllOauthConfigs();
		model.addAttribute("oauthConfigs", oauthConfigs);
		if ("Existing".equalsIgnoreCase(form.getSaveCfg())) {
			model.addAttribute("extCfgs", this.getExistingConfigs(oauthConfigs));
		}
	}

	@ExceptionHandler(Exception.class)
	public ModelAndView handleError(HttpServletRequest req, Exception ex) {
		LOGGER.error("Request: {} have raised an error", req.getRequestURI(), ex);
		ModelAndView mav = new ModelAndView();
		mav.addObject("errorDesc", ex.getMessage());
		mav.addObject("url", req.getRequestURL());
		mav.setViewName("asr/common/error.html");
		return mav;
	}

	protected boolean validate(CreateOauthForm form) {
		return StringUtils.isNotEmpty(form.getAuthUri()) && StringUtils.isNotEmpty(form.getClientId())
				&& StringUtils.isNotEmpty(form.getClientSecret())
				&& (StringUtils.isNotEmpty(form.getConfigName()) || StringUtils.isNotEmpty(form.getAddtoConfigId()))
				&& StringUtils.isNotEmpty(form.getJwksUri()) && StringUtils.isNotEmpty(form.getRegId())
				&& StringUtils.isNotEmpty(form.getTokenUri()) && StringUtils.isNotEmpty(form.getUserInfoUri());
	}

}
