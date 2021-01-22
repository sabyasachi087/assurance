package com.sroyc.assurance.core.sso;

import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.sroyc.assurance.core.AssuranceConfigurationRepository;
import com.sroyc.assurance.core.Resetable;
import com.sroyc.assurance.core.data.AssuranceSSOConfig;
import com.sroyc.assurance.core.data.SSOType;
import com.sroyc.assurance.core.exception.AssuranceConfigurationException;
import com.sroyc.assurance.core.exception.AssuranceEntityException;
import com.sroyc.assurance.core.exception.ResetFailureException;

public abstract class SSOConfiguration implements Resetable {

	private AssuranceProviderManager provider;
	private AssuranceConfigurationRepository configRepo;
	private AssuranceSSOConfig ssoConfig;
	private Boolean active = Boolean.FALSE;

	protected ApplicationContext context;

	protected SSOConfiguration(AssuranceConfigurationRepository configRepo, AssuranceProviderManager provider,
			ApplicationContext ctx) {
		this.provider = provider;
		this.configRepo = configRepo;
		this.context = ctx;
	}

	/** Must be invoked during reset with corresponding {@linkplain SSOType} */
	protected synchronized void init(SSOType type) throws ResetFailureException {
		this.ssoConfig = null;
		try {
			this.active = Boolean.FALSE;
			AssuranceSSOConfig config = this.configRepo.findActiveSSOConfig(type);
			if (config == null && type == SSOType.BASIC) {
				this.persistBasicConfig();
			} else if (config != null && type == config.getType()) {
				this.ssoConfig = config;
				this.configure();
				this.active = Boolean.TRUE;
				getAuthManager().add(type, this.authProvider());
			} else {
				this.provider.remove(type);
			}
		} catch (Exception ex) {
			throw new ResetFailureException(ex);
		}
	}

	// Save this information
	private void persistBasicConfig() throws AssuranceEntityException {
		// Must save basic type configuration
		List<AssuranceSSOConfig> basicConfigs = this.configRepo.findSSOConfigs(SSOType.BASIC);
		if (CollectionUtils.isEmpty(basicConfigs)) {
			AssuranceSSOConfig basicConfig = new AssuranceSSOConfig();
			basicConfig.setActive(true);
			basicConfig.setType(SSOType.BASIC);
			basicConfig.setName("Deafult_Basic");
			this.configRepo.save(basicConfig);
			try {
				// re-initialize
				this.init(SSOType.BASIC);
			} catch (Exception ex) {
			}
		}
	}

	protected final AssuranceProviderManager getAuthManager() {
		return this.provider;
	}

	protected final AssuranceConfigurationRepository getConfigRepo() {
		return this.configRepo;
	}

	protected final AssuranceSSOConfig getSSOConfig() {
		return this.ssoConfig;
	}

	public boolean isActive() {
		return this.active.booleanValue();
	}

	/** Configure SSO configuration */
	protected abstract void configure();

	public abstract AuthenticationEntryPoint entryPoint();

	public abstract Filter filter();

	public abstract AuthenticationProvider authProvider();

	/**
	 * Request path to be matched during filter invocation. The configuration will
	 * be invoked only if the path matched with incoming request
	 */
	public abstract RequestMatcher matcher();

	/**
	 * Optional filter and SAML configuration should override for metadata generator
	 * 
	 * @throws AssuranceConfigurationException
	 */
	public GenericFilterBean channelProcessingFilter() {
		return null;
	}

	protected AssuranceSuccessHandler getSuccessHandler() {
		return this.context.getBean(AssuranceSuccessHandler.class);
	}

	/**
	 * Returns instance of {@linkplain AssuranceUserDetailsService} if found, null
	 * otherwise
	 */
	protected AssuranceUserDetailsService getUserDetailsService() {
		try {
			return this.context.getBean(AssuranceUserDetailsService.class);
		} catch (NoSuchBeanDefinitionException nsdbe) {
		}
		return null;
	}

}
