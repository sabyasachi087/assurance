package com.sroyc.assurance.core.oauth;

import javax.servlet.Filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.sroyc.assurance.core.AssuranceConfigurationRepository;
import com.sroyc.assurance.core.data.SSOType;
import com.sroyc.assurance.core.exception.ResetFailureException;
import com.sroyc.assurance.core.sso.AssuranceFailureHandler;
import com.sroyc.assurance.core.sso.AssuranceProviderManager;
import com.sroyc.assurance.core.sso.SSOConfiguration;
import com.sroyc.assurance.core.util.AssuranceCommonUtil;

@Component("oauth2SSOConfiguration")
public class OAuth2SSOConfiguration extends SSOConfiguration {

	private static final Logger LOGGER = LogManager.getLogger(OAuth2SSOConfiguration.class);

	private Oauth2SSORuntimeConfigurer configurer;
	private AssuranceFailureHandler failureHandler;
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

	@Autowired
	public OAuth2SSOConfiguration(AssuranceConfigurationRepository configRepo, AssuranceProviderManager provider,
			AssuranceFailureHandler failureHandler, ApplicationContext context) {
		super(configRepo, provider, context);
		this.failureHandler = failureHandler;
	}

	@Override
	public void reset() throws ResetFailureException {
		this.init(SSOType.OAUTH);
	}

	@Override
	protected void configure() {
		ClientRegistrationRepository clientRegRepo = new InMemoryClientRegistrationRepository(this.getSSOConfig());
		OAuth2AuthorizedClientService clientService = new AssuranceDefaultCaffeineClientService(clientRegRepo);
		this.setUserService();
		this.configurer = new Oauth2SSORuntimeConfigurer(clientRegRepo, clientService, this.getAuthManager(),
				this.getSuccessHandler(), this.failureHandler, this.userService);
	}

	@Override
	public AuthenticationEntryPoint entryPoint() {
		return this.configurer.loginEntryPoint();
	}

	@Override
	public Filter filter() {
		return this.configurer.oauthFilter();
	}

	@Override
	public AuthenticationProvider authProvider() {
		return this.configurer.authenticationProvider();
	}

	@Override
	public RequestMatcher matcher() {
		return this.configurer.requestMatcher();
	}

	@SuppressWarnings("unchecked")
	protected void setUserService() {
		if ((this.userService = AssuranceCommonUtil.getBeanByClass(OAuth2UserService.class, this.context)) == null) {
			LOGGER.warn("Unable to find beans of type [{}] or [{}]. Falling back to default implementation",
					OAuth2UserService.class.getCanonicalName(), AssuranceOAuth2UserService.class.getCanonicalName());
			this.userService = new DefaultAssuranceOAuth2UserService();
		}
	}

}
