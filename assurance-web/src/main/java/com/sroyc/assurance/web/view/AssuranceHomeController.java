package com.sroyc.assurance.web.view;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.exception.AssuranceConfigurationException;
import com.sroyc.assurance.core.exception.AssuranceEntityException;
import com.sroyc.assurance.core.exception.AssuranceRuntimeException;
import com.sroyc.assurance.core.exception.ResetFailureException;
import com.sroyc.assurance.web.service.AssuranceWebService;
import com.sroyc.assurance.web.util.AssuranceWebConstants;
import com.sroyc.noderegistrar.main.NodeCommandPublisher;

@Controller
@RequestMapping("/assurance")
public class AssuranceHomeController extends HeaderView {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceHomeController.class);

	private AssuranceWebService service;
	private NodeCommandPublisher commandPublisher;

	@Autowired
	public AssuranceHomeController(AssuranceConfigurationProperty config, AssuranceWebService service,
			NodeCommandPublisher commandPublisher) {
		super(config);
		this.service = service;
		this.commandPublisher = commandPublisher;
	}

	@GetMapping("/home")
	public String home(Model model) {
		this.loadBasicData(model);
		return "asr/home.html";
	}

	protected void loadBasicData(Model model) {
		try {
			this.loadHeader(model);
			model.addAttribute("configs", this.service.getAllConfigs());
		} catch (Exception ex) {
			throw new AssuranceRuntimeException(ex);
		}
	}

	@PostMapping("/config/activation/toggle")
	public String toggleConfigActivation(HttpServletRequest request, Model model)
			throws AssuranceEntityException, ResetFailureException {
		String configIdToToggle = request.getParameter("configIdToToggle");
		try {
			this.service.toggleActivation(configIdToToggle);
			model.addAttribute("successMsg", "Configuration updated successfully");
		} catch (AssuranceConfigurationException ace) {
			model.addAttribute("errorMsg", ace.getMessage());
		}
		this.loadBasicData(model);
		return "asr/home.html";
	}

	@PostMapping("/config/reset")
	public String resetConfig(Model model) throws AssuranceEntityException, ResetFailureException {
		// this.service.resetConfig();
		this.commandPublisher.publish(AssuranceWebConstants.SSO_RESET_COMMAND);
		model.addAttribute("successMsg", "Command has been registered and will be applied eventually");
		this.loadBasicData(model);
		return "asr/home.html";
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

}
