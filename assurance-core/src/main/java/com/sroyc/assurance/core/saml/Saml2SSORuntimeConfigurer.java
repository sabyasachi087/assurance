package com.sroyc.assurance.core.saml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.ParserPool;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.SAMLWebSSOHoKProcessingFilter;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.CachingMetadataManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.processor.HTTPArtifactBinding;
import org.springframework.security.saml.processor.HTTPPAOS11Binding;
import org.springframework.security.saml.processor.HTTPPostBinding;
import org.springframework.security.saml.processor.HTTPRedirectDeflateBinding;
import org.springframework.security.saml.processor.HTTPSOAP11Binding;
import org.springframework.security.saml.processor.SAMLBinding;
import org.springframework.security.saml.processor.SAMLProcessorImpl;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.security.saml.websso.ArtifactResolutionProfile;
import org.springframework.security.saml.websso.ArtifactResolutionProfileImpl;
import org.springframework.security.saml.websso.SingleLogoutProfile;
import org.springframework.security.saml.websso.SingleLogoutProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfile;
import org.springframework.security.saml.websso.WebSSOProfileConsumer;
import org.springframework.security.saml.websso.WebSSOProfileConsumerHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileConsumerImpl;
import org.springframework.security.saml.websso.WebSSOProfileECPImpl;
import org.springframework.security.saml.websso.WebSSOProfileHoKImpl;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import com.sroyc.assurance.core.sso.AssuranceProviderManager;
import com.sroyc.assurance.core.sso.AssuranceSSOConfigurer;
import com.sroyc.assurance.core.sso.AssuranceSuccessHandler;

public class Saml2SSORuntimeConfigurer implements AssuranceSSOConfigurer {

	private SAMLAuthenticationProvider samlAuthenticationProvider;
	private SAMLContextProviderImpl samlContextProvider;
	private WebSSOProfileConsumer webSSOprofileConsumer;
	private WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer;
	private WebSSOProfile webSSOprofile;
	private WebSSOProfileECPImpl ecpprofile;
	private SingleLogoutProfile logoutprofile;
	private WebSSOProfileOptions webSSOProfileOptions;
	private SAMLEntryPoint samlEntryPoint;
	private ExtendedMetadata extendedMetadata;
	private SAMLDiscovery idpDiscovery;
	private CachingMetadataManager cachingMetadataManager;
	private MetadataGenerator metadataGenerator;
	private MetadataGeneratorFilter metadataGeneratorFilter;
	private SimpleUrlAuthenticationFailureHandler authenticationFailureHandler;
	private SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter;
	private SAMLProcessingFilter samlWebSSOProcessingFilter;
	private SimpleUrlLogoutSuccessHandler successLogoutHandler;
	private SecurityContextLogoutHandler logoutHandler;
	private SAMLProcessorImpl samlProcessor;
	private FilterChainProxy samlFilter;
	private ExtendedMetadataDelegate idpMetaDataProvider;
	private SAMLLogoutProcessingFilter samlLogoutProcessingFilter;

	private AssuranceProviderManager authManager;
	private SAMLUserDetailsService samlUserDetailsService;
	private Saml2BeanConfiguration httpConfig;
	private AssuranceKeyManager keyStore;
	private SamlConfiguration samlConfig;
	private AssuranceSuccessHandler successHandler;

	protected Saml2SSORuntimeConfigurer(AssuranceProviderManager authManager,
			SAMLUserDetailsService samlUserDetailsService, Saml2BeanConfiguration httpConfig,
			AssuranceKeyManager keyStore, SamlConfiguration samlConfig, AssuranceSuccessHandler successHandler)
			throws Exception {
		this.authManager = authManager;
		this.samlUserDetailsService = samlUserDetailsService;
		this.httpConfig = httpConfig;
		this.keyStore = keyStore;
		Assert.notNull(samlConfig, "Saml configuration is null");
		this.samlConfig = samlConfig;
		this.successHandler = successHandler;
	}

	public SAMLAuthenticationProvider samlAuthenticationProvider() throws Exception {
		if (this.samlAuthenticationProvider == null) {
			this.samlAuthenticationProvider = new SAMLAuthenticationProvider();
			this.samlAuthenticationProvider.setUserDetails(samlUserDetailsService);
			this.samlAuthenticationProvider.setForcePrincipalAsString(false);
			this.samlAuthenticationProvider.setSamlLogger(this.samlLogger());
			this.samlAuthenticationProvider.setConsumer(this.webSSOprofileConsumer());
			this.samlAuthenticationProvider.setHokConsumer(this.hokWebSSOprofileConsumer());
		}
		return this.samlAuthenticationProvider;
	}

	// Provider of default SAML Context
	public SAMLContextProviderImpl contextProvider() throws Exception {
		if (this.samlContextProvider == null) {
			this.samlContextProvider = new SAMLContextProviderImpl();
			this.samlContextProvider.setKeyManager(keyManager());
			this.samlContextProvider.setMetadata(metadata());
			this.samlContextProvider.afterPropertiesSet();
		}
		return this.samlContextProvider;
	}

	// SAML 2.0 WebSSO Assertion Consumer
	public WebSSOProfileConsumer webSSOprofileConsumer() throws MetadataProviderException {
		if (this.webSSOprofileConsumer == null) {
			this.webSSOprofileConsumer = new WebSSOProfileConsumerImpl(this.processor(), this.metadata());
		}
		return this.webSSOprofileConsumer;
	}

	// SAML 2.0 Holder-of-Key WebSSO Assertion Consumer
	public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
		if (this.hokWebSSOprofileConsumer == null) {
			this.hokWebSSOprofileConsumer = new WebSSOProfileConsumerHoKImpl();
		}
		return this.hokWebSSOprofileConsumer;
	}

	// SAML 2.0 Web SSO profile
	public WebSSOProfile webSSOprofile() throws MetadataProviderException {
		if (this.webSSOprofile == null) {
			this.webSSOprofile = new WebSSOProfileImpl(this.processor(), this.metadata());
		}
		return this.webSSOprofile;
	}

	// SAML 2.0 Holder-of-Key Web SSO profile
	public WebSSOProfileHoKImpl hokWebSSOProfile() {
		return new WebSSOProfileHoKImpl();
	}

	// SAML 2.0 ECP profile
	public WebSSOProfileECPImpl ecpprofile() {
		if (this.ecpprofile == null) {
			this.ecpprofile = new WebSSOProfileECPImpl();
		}
		return this.ecpprofile;
	}

	public SingleLogoutProfile logoutprofile() {
		if (this.logoutprofile == null) {
			this.logoutprofile = new SingleLogoutProfileImpl();
		}
		return this.logoutprofile;
	}

	public KeyManager keyManager() {
		return this.keyStore.getKeyManager();
	}

	public WebSSOProfileOptions defaultWebSSOProfileOptions() {
		if (this.webSSOProfileOptions == null) {
			this.webSSOProfileOptions = new WebSSOProfileOptions();
			this.webSSOProfileOptions.setIncludeScoping(false);
		}
		return this.webSSOProfileOptions;
	}

	public SAMLEntryPoint samlEntryPoint() throws Exception {
		if (this.samlEntryPoint == null) {
			this.samlEntryPoint = new SAMLEntryPoint();
			this.samlEntryPoint.setDefaultProfileOptions(defaultWebSSOProfileOptions());
			this.samlEntryPoint.setWebSSOprofile(webSSOprofile());
			this.samlEntryPoint.setWebSSOprofileECP(ecpprofile());
			this.samlEntryPoint.setWebSSOprofileHoK(hokWebSSOProfile());
			this.samlEntryPoint.setMetadata((MetadataManager) metadata());
			this.samlEntryPoint.setSamlDiscovery(samlIDPDiscovery());
			this.samlEntryPoint.setSamlLogger(samlLogger());
			this.samlEntryPoint.setContextProvider(contextProvider());
		}
		return this.samlEntryPoint;
	}

	public SAMLDefaultLogger samlLogger() {
		return new SAMLDefaultLogger();
	}

	public ExtendedMetadata extendedMetadata(String sigingAlgorithm, boolean signMetadata) {
		if (this.extendedMetadata == null) {
			this.extendedMetadata = new ExtendedMetadata();
			// this.extendedMetadata.setIdpDiscoveryEnabled(true);
			if (signMetadata) {
				this.extendedMetadata.setSigningAlgorithm(sigingAlgorithm);
				this.extendedMetadata.setSignMetadata(true);
			}
			this.extendedMetadata.setEcpEnabled(true);
		}
		return this.extendedMetadata;
	}

	public SAMLDiscovery samlIDPDiscovery() throws Exception {
		if (this.idpDiscovery == null) {
			this.idpDiscovery = new SAMLDiscovery();
			this.idpDiscovery.setIdpSelectionPath("/saml/discovery");
			this.idpDiscovery.setContextProvider(this.contextProvider());
		}
		return this.idpDiscovery;
	}

	public ExtendedMetadataDelegate idpMetaDataProvider() {
		if (this.idpMetaDataProvider == null) {
			AssuranceMetadataProvider provider = new AssuranceMetadataProvider(this.samlConfig);
			provider.setParserPool(this.httpConfig.parserPool());
			this.idpMetaDataProvider = new ExtendedMetadataDelegate(provider,
					extendedMetadata(this.samlConfig.getSigningAlogrithm().signingAlgorithm(), true));
			this.idpMetaDataProvider.setMetadataTrustCheck(true);
			this.idpMetaDataProvider.setMetadataRequireSignature(false);
			this.httpConfig.getBackgroundTaskTimer().purge();
		}
		return this.idpMetaDataProvider;
	}

	public CachingMetadataManager metadata() throws MetadataProviderException {
		if (this.cachingMetadataManager == null) {
			List<MetadataProvider> providers = new ArrayList<>();
			providers.add(idpMetaDataProvider());
			this.cachingMetadataManager = new CachingMetadataManager(providers);
			this.cachingMetadataManager.setKeyManager(keyManager());
			this.cachingMetadataManager.refreshMetadata();
		}
		return this.cachingMetadataManager;
	}

	public MetadataGenerator metadataGenerator() throws Exception {
		if (this.metadataGenerator == null) {
			this.metadataGenerator = new MetadataGenerator();
			this.metadataGenerator.setEntityId(this.samlConfig.getEntityId());
			this.metadataGenerator.setExtendedMetadata(
					extendedMetadata(this.samlConfig.getSigningAlogrithm().signingAlgorithm(), true));
			this.metadataGenerator.setIncludeDiscoveryExtension(false);
			this.metadataGenerator.setKeyManager(keyManager());
			this.metadataGenerator.setSamlWebSSOHoKFilter(samlWebSSOHoKProcessingFilter());
			this.metadataGenerator.setSamlWebSSOFilter(this.samlWebSSOProcessingFilter());
			this.metadataGenerator.setSamlLogoutProcessingFilter(samlLogoutProcessingFilter());
			this.metadataGenerator.setSamlEntryPoint(this.samlEntryPoint());
		}
		return this.metadataGenerator;
	}

	public MetadataDisplayFilter metadataDisplayFilter() {
		return new MetadataDisplayFilter();
	}

	public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
		if (this.authenticationFailureHandler == null) {
			this.authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
			this.authenticationFailureHandler.setUseForward(true);
			this.authenticationFailureHandler.setDefaultFailureUrl("/error");
		}
		return this.authenticationFailureHandler;
	}

	public SAMLWebSSOHoKProcessingFilter samlWebSSOHoKProcessingFilter() {
		if (this.samlWebSSOHoKProcessingFilter == null) {
			this.samlWebSSOHoKProcessingFilter = new SAMLWebSSOHoKProcessingFilter();
			this.samlWebSSOHoKProcessingFilter.setAuthenticationSuccessHandler(this.successHandler);
			this.samlWebSSOHoKProcessingFilter.setAuthenticationManager(this.authManager);
			this.samlWebSSOHoKProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
		}
		return samlWebSSOHoKProcessingFilter;
	}

	public SAMLProcessingFilter samlWebSSOProcessingFilter() throws Exception {
		if (this.samlWebSSOProcessingFilter == null) {
			this.samlWebSSOProcessingFilter = new SAMLProcessingFilter();
			this.samlWebSSOProcessingFilter.setAuthenticationManager(this.authManager);
			this.samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(this.successHandler);
			this.samlWebSSOProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
			this.samlWebSSOProcessingFilter.setContextProvider(this.contextProvider());
			this.samlWebSSOProcessingFilter.setSAMLProcessor(this.processor());
		}
		return this.samlWebSSOProcessingFilter;
	}

	public MetadataGeneratorFilter metadataGeneratorFilter() throws Exception {
		if (this.metadataGeneratorFilter == null) {
			this.metadataGeneratorFilter = new MetadataGeneratorFilter(metadataGenerator());
			this.metadataGeneratorFilter.setDisplayFilter(metadataDisplayFilter());
			this.metadataGeneratorFilter.setManager(metadata());
		}
		return this.metadataGeneratorFilter;
	}

	public SimpleUrlLogoutSuccessHandler successLogoutHandler() {
		if (this.successLogoutHandler == null) {
			this.successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
			this.successLogoutHandler.setDefaultTargetUrl("/");
		}
		return this.successLogoutHandler;
	}

	public SecurityContextLogoutHandler logoutHandler() {
		if (this.logoutHandler == null) {
			this.logoutHandler = new SecurityContextLogoutHandler();
			this.logoutHandler.setInvalidateHttpSession(true);
			this.logoutHandler.setClearAuthentication(true);
		}
		return this.logoutHandler;
	}

	public SAMLLogoutProcessingFilter samlLogoutProcessingFilter() throws Exception {
		if (this.samlLogoutProcessingFilter == null) {
			this.samlLogoutProcessingFilter = new SAMLLogoutProcessingFilter(successLogoutHandler(), logoutHandler());
			this.samlLogoutProcessingFilter.setContextProvider(this.contextProvider());
			this.samlLogoutProcessingFilter.setSAMLProcessor(this.processor());
			this.samlLogoutProcessingFilter.setLogoutProfile(new SingleLogoutProfileImpl());
			this.samlLogoutProcessingFilter.setSamlLogger(this.samlLogger());
			this.samlLogoutProcessingFilter.afterPropertiesSet();
		}
		return this.samlLogoutProcessingFilter;
	}

	public SAMLLogoutFilter samlLogoutFilter() {
		return new SAMLLogoutFilter(successLogoutHandler(), new LogoutHandler[] { logoutHandler() },
				new LogoutHandler[] { logoutHandler() });
	}

	private ArtifactResolutionProfile artifactResolutionProfile() {
		final ArtifactResolutionProfileImpl artifactResolutionProfile = new ArtifactResolutionProfileImpl(
				this.httpConfig.httpClient());
		artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(soapBinding()));
		return artifactResolutionProfile;
	}

	public HTTPArtifactBinding artifactBinding(ParserPool parserPool, VelocityEngine velocityEngine) {
		return new HTTPArtifactBinding(parserPool, velocityEngine, artifactResolutionProfile());
	}

	public HTTPSOAP11Binding soapBinding() {
		return new HTTPSOAP11Binding(this.httpConfig.parserPool());
	}

	public HTTPPostBinding httpPostBinding() {
		return new HTTPPostBinding(this.httpConfig.parserPool(), this.httpConfig.velocityEngine());
	}

	public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
		return new HTTPRedirectDeflateBinding(this.httpConfig.parserPool());
	}

	public HTTPSOAP11Binding httpSOAP11Binding() {
		return new HTTPSOAP11Binding(this.httpConfig.parserPool());
	}

	public HTTPPAOS11Binding httpPAOS11Binding() {
		return new HTTPPAOS11Binding(this.httpConfig.parserPool());
	}

	public SAMLProcessorImpl processor() {
		if (this.samlProcessor == null) {
			Collection<SAMLBinding> bindings = new ArrayList<>();
			bindings.add(httpRedirectDeflateBinding());
			bindings.add(httpPostBinding());
			bindings.add(artifactBinding(this.httpConfig.parserPool(), this.httpConfig.velocityEngine()));
			bindings.add(httpSOAP11Binding());
			bindings.add(httpPAOS11Binding());
			this.samlProcessor = new SAMLProcessorImpl(bindings);
		}
		return this.samlProcessor;
	}

	public FilterChainProxy samlFilter() throws Exception {
		if (this.samlFilter == null) {
			List<SecurityFilterChain> chains = new ArrayList<>();
			chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint()));
			chains.add(
					new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"), samlLogoutFilter()));
			chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/metadata/**"),
					metadataDisplayFilter()));
			chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"),
					samlWebSSOProcessingFilter()));
			chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSOHoK/**"),
					samlWebSSOHoKProcessingFilter()));
			chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"),
					samlLogoutProcessingFilter()));
			chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"),
					samlIDPDiscovery()));
			this.samlFilter = new FilterChainProxy(chains);
		}
		return this.samlFilter;
	}

}
