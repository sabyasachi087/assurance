package com.sroyc.assurance.core;

import java.util.List;

import com.sroyc.assurance.core.data.AssuranceSSOConfig;
import com.sroyc.assurance.core.data.SSOType;
import com.sroyc.assurance.core.exception.AssuranceEntityException;

public interface AssuranceConfigurationRepository {

	/**
	 * Fetches active sso configuration.
	 * 
	 * @return {@linkplain AssuranceSSOConfig} if found, <b>null otherwise</b>
	 */
	public AssuranceSSOConfig findActiveSSOConfig(SSOType type) throws AssuranceEntityException;

	public List<AssuranceSSOConfig> findSSOConfigs(SSOType type) throws AssuranceEntityException;

	public List<AssuranceSSOConfig> findAll() throws AssuranceEntityException;

	public String save(AssuranceSSOConfig config) throws AssuranceEntityException;

	public void delete(String id) throws AssuranceEntityException;

	boolean update(String configId, boolean active);

}
