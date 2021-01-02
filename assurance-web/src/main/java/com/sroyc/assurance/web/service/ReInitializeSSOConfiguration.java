package com.sroyc.assurance.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sroyc.assurance.web.service.AssuranceNodeCommandListener.AssuranceCommandExecutor;
import com.sroyc.assurance.web.util.AssuranceWebConstants;
import com.sroyc.noderegistrar.exception.CommandExecutionFailuer;

@Component
public class ReInitializeSSOConfiguration implements AssuranceCommandExecutor {

	private AssuranceWebService service;

	@Autowired
	public ReInitializeSSOConfiguration(AssuranceWebService service) {
		this.service = service;
	}

	@Override
	public void execute() throws CommandExecutionFailuer {
		try {
			this.service.resetConfig();
		} catch (Exception e) {
			throw new CommandExecutionFailuer(e);
		}
	}

	@Override
	public String command() {
		return AssuranceWebConstants.SSO_RESET_COMMAND;
	}

}
