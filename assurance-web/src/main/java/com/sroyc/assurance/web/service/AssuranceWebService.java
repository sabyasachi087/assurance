package com.sroyc.assurance.web.service;

import java.util.List;

import com.sroyc.assurance.core.exception.AssuranceEntityException;
import com.sroyc.assurance.core.exception.ResetFailureException;
import com.sroyc.assurance.web.view.model.CreateOauthForm;
import com.sroyc.assurance.web.view.model.CreateSamlForm;
import com.sroyc.assurance.web.view.model.OauthConfigurationVO;
import com.sroyc.assurance.web.view.model.SamlConfigurationVO;
import com.sroyc.assurance.web.view.model.ViewAssuranceConfigVO;

public interface AssuranceWebService {

	String saveSamlConfig(CreateSamlForm form, byte[] certificate) throws AssuranceEntityException;

	List<SamlConfigurationVO> getAllSamlConfigs() throws AssuranceEntityException;

	void deleteConfig(String configId) throws AssuranceEntityException;

	List<ViewAssuranceConfigVO> getAllConfigs() throws AssuranceEntityException;

	void toggleActivation(String configId) throws AssuranceEntityException;

	void resetConfig() throws AssuranceEntityException, ResetFailureException;

	String saveOauthConfig(CreateOauthForm form) throws AssuranceEntityException;

	List<OauthConfigurationVO> getAllOauthConfigs() throws AssuranceEntityException;

}
