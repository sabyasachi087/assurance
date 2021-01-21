package com.sroyc.assurance.core.saml;

import javax.servlet.Filter;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.sroyc.assurance.core.AssuranceConfigurationRepository;
import com.sroyc.assurance.core.data.SSOType;
import com.sroyc.assurance.core.exception.AssuranceConfigurationException;
import com.sroyc.assurance.core.exception.AssuranceRuntimeException;
import com.sroyc.assurance.core.exception.ResetFailureException;
import com.sroyc.assurance.core.sso.AssuranceProviderManager;
import com.sroyc.assurance.core.sso.DefaultAssuranceSAMLUserDetailsService;
import com.sroyc.assurance.core.sso.SSOConfiguration;

@Component("saml2SSOConfiguration")
public class Saml2SSOConfiguration extends SSOConfiguration {

	private Saml2SSORuntimeConfigurer configurer;
	private SAMLUserDetailsService userService;
	private Saml2BeanConfiguration saml2Bean;

	@Autowired
	public Saml2SSOConfiguration(AssuranceConfigurationRepository configRepo, AssuranceProviderManager provider,
			ApplicationContext context, Saml2BeanConfiguration saml2Bean) {
		super(configRepo, provider, context);
		this.saml2Bean = saml2Bean;
	}

	protected void configure() {
		try {
			AssuranceKeyManager keyManager = new AssuranceKeyManager(getSSOConfig());
			this.setUserService();
			this.configurer = new Saml2SSORuntimeConfigurer(getAuthManager(), this.userService, this.saml2Bean,
					keyManager, (SamlConfiguration) getSSOConfig().getMetadata(), this.getSuccessHandler());
			this.samlBootstrap();
		} catch (Exception ex) {
			throw new AssuranceConfigurationException(ex);
		}
	}

	public void samlBootstrap() {
		ConfigurableApplicationContext configContext = (ConfigurableApplicationContext) this.context;
		SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();

		if (!beanRegistry.containsSingleton("samlBootstrap")) {
			SAMLBootstrap sb = new SAMLBootstrap();
			beanRegistry.registerSingleton("samlBootstrap", sb);
			sb.postProcessBeanFactory((ConfigurableListableBeanFactory) this.context.getAutowireCapableBeanFactory());
		}
	}

	protected void setUserService() {
		try {
			this.userService = this.context.getBean(AssuranceSAMLUserDetailsService.class);
		} catch (NoSuchBeanDefinitionException nsdbe) {
			this.userService = new DefaultAssuranceSAMLUserDetailsService();
		}
	}

	@Override
	public void reset() throws ResetFailureException {
		this.init(SSOType.SAML);
	}

	@Override
	public AuthenticationEntryPoint entryPoint() {
		try {
			return this.configurer.samlEntryPoint();
		} catch (Exception e) {
			throw new AssuranceConfigurationException(e);
		}
	}

	@Override
	public Filter filter() {
		try {
			return this.configurer.samlFilter();
		} catch (Exception e) {
			throw new AssuranceConfigurationException(e);
		}
	}

	@Override
	public AuthenticationProvider authProvider() {
		try {
			return this.configurer.samlAuthenticationProvider();
		} catch (Exception e) {
			throw new AssuranceRuntimeException(e);
		}
	}

	@Override
	public RequestMatcher matcher() {
		return new AntPathRequestMatcher("/saml/**");
	}

	@Override
	public GenericFilterBean channelProcessingFilter() {
		try {
			return this.configurer.metadataGeneratorFilter();
		} catch (Exception e) {
			throw new AssuranceConfigurationException(e);
		}
	}

}
