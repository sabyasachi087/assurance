package com.sroyc.assurance.web.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.data.AssuranceKeyStore.CertificateFormat;
import com.sroyc.assurance.core.exception.AssuranceEntityException;
import com.sroyc.assurance.core.saml.RSASigningAlgorithm;
import com.sroyc.assurance.web.service.AssuranceWebService;
import com.sroyc.assurance.web.view.model.CreateSamlForm;

@Controller
@RequestMapping("/assurance/saml")
public class AssuranceSamlController extends HeaderView {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceSamlController.class);
	private static final String FORM_NAME = "createSamlForm";
	private static final String PAGE_URL = "asr/saml/saml.html";

	private AssuranceWebService service;

	@Autowired
	public AssuranceSamlController(AssuranceConfigurationProperty config, AssuranceWebService service) {
		super(config);
		this.service = service;
	}

	@GetMapping
	public String getCreateConfigPage(Model model) throws AssuranceEntityException {
		this.loadBaseAttributes(model);
		return PAGE_URL;
	}

	@PostMapping
	public String createConfigPage(@ModelAttribute CreateSamlForm form, @RequestParam("certificate") MultipartFile file,
			Model model) throws AssuranceEntityException, IOException {
		if (this.validate(form, file, model)) {
			this.service.saveSamlConfig(form, file.getBytes());
			model.addAttribute("successMsg", "Configuration saved successfully");
			this.loadBaseAttributes(model);
		} else {
			model.addAttribute(FORM_NAME, form);
			this.loadBaseAttributes(model);
		}
		return PAGE_URL;
	}

	@PostMapping("/delete")
	public String deleteConfig(Model model, HttpServletRequest request) throws AssuranceEntityException {
		String configIdToBeDeleted = request.getParameter("configIdToBeDeleted");
		this.service.deleteConfig(configIdToBeDeleted);
		model.addAttribute("successMsg",
				"Configuration Id [" + configIdToBeDeleted + "] has been removed successfully");
		this.loadBaseAttributes(model);
		return PAGE_URL;
	}

	protected List<String> getSigninAlgos() {
		List<String> algos = new ArrayList<>();
		algos.add("");
		for (RSASigningAlgorithm algo : RSASigningAlgorithm.values()) {
			algos.add(algo.name());
		}
		return algos;
	}

	protected List<String> getCertificateFormats() {
		List<String> cfs = new ArrayList<>();
		cfs.add("");
		for (CertificateFormat cf : CertificateFormat.values()) {
			cfs.add(cf.name());
		}
		return cfs;
	}

	private void loadBaseAttributes(Model model) throws AssuranceEntityException {
		model.addAttribute("algos", this.getSigninAlgos());
		model.addAttribute("certFormats", this.getCertificateFormats());
		model.addAttribute("samlConfigs", this.service.getAllSamlConfigs());
		CreateSamlForm form = new CreateSamlForm();
		model.addAttribute(FORM_NAME, form);
		this.loadHeader(model);
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

	protected boolean validate(CreateSamlForm form, MultipartFile file, Model model) {
		if (!StringUtils.hasLength(form.getCertAlias()) || !StringUtils.hasLength(form.getCertificateType())
				|| !StringUtils.hasLength(form.getCertPwd()) || !StringUtils.hasLength(form.getConfigName())
				|| !StringUtils.hasLength(form.getEntityId()) || !StringUtils.hasLength(form.getIdpMetadata())
				|| !StringUtils.hasLength(form.getSignAlgo()) || file.isEmpty()) {
			model.addAttribute("errorMsg", "All fields are mandatory. Please validate the form");
			return false;
		}
		return true;
	}

}
