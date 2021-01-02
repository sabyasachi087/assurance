package com.sroyc.assurance.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sroyc.assurance.core.AssuranceConfigurationFactory;
import com.sroyc.assurance.core.data.AssuranceKeyStore;
import com.sroyc.assurance.core.data.AssuranceKeyStore.CertificateFormat;
import com.sroyc.assurance.core.data.AssuranceSSOConfig;
import com.sroyc.assurance.core.data.SSOType;
import com.sroyc.assurance.core.exception.AssuranceConfigurationException;
import com.sroyc.assurance.core.exception.AssuranceEntityException;
import com.sroyc.assurance.core.exception.ResetFailureException;
import com.sroyc.assurance.core.oauth.Oauth2Configuration;
import com.sroyc.assurance.core.oauth.OauthClientRegister;
import com.sroyc.assurance.core.saml.RSASigningAlgorithm;
import com.sroyc.assurance.core.saml.SamlConfiguration;
import com.sroyc.assurance.web.mongo.AssuranceMongoConfigurationRepository;
import com.sroyc.assurance.web.mongo.MongoSSOConfigEntity;
import com.sroyc.assurance.web.util.UniqueSequenceGenerator;
import com.sroyc.assurance.web.view.model.CreateOauthForm;
import com.sroyc.assurance.web.view.model.CreateSamlForm;
import com.sroyc.assurance.web.view.model.OauthConfigurationVO;
import com.sroyc.assurance.web.view.model.OauthConfigurationVO.ClientRegistrationVO;
import com.sroyc.assurance.web.view.model.SamlConfigurationVO;
import com.sroyc.assurance.web.view.model.ViewAssuranceConfigVO;

@Service
public class AssuranceWebServiceImpl implements AssuranceWebService {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceWebServiceImpl.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();

	private AssuranceMongoConfigurationRepository configRepo;
	private AssuranceConfigurationFactory asrConfigFactory;

	@Autowired
	public AssuranceWebServiceImpl(AssuranceMongoConfigurationRepository configRepo,
			AssuranceConfigurationFactory asrConfigFactory) {
		super();
		this.configRepo = configRepo;
		this.asrConfigFactory = asrConfigFactory;
	}

	@Override
	public String saveSamlConfig(CreateSamlForm form, byte[] certificate) throws AssuranceEntityException {
		MongoSSOConfigEntity config = this.createSSOConfig(SSOType.SAML, form.getConfigName());
		config.setMetadata(this.createSamlConfig(form, certificate));
		return this.configRepo.save(config);
	}

	protected MongoSSOConfigEntity createSSOConfig(SSOType type, String configName) {
		MongoSSOConfigEntity config = new MongoSSOConfigEntity();
		config.setActive(false);
		config.autoGenerateId();
		config.setType(type);
		config.setName(configName);
		return config;
	}

	protected SamlConfiguration createSamlConfig(CreateSamlForm form, byte[] certificate) {
		SamlConfiguration config = new SamlConfiguration();
		config.setEntityId(form.getEntityId());
		config.setIdpMetadata(form.getIdpMetadata());
		AssuranceKeyStore ks = new AssuranceKeyStore();
		ks.setAlias(form.getCertAlias());
		ks.setCertificate(certificate);
		ks.setFormat(CertificateFormat.valueOf(form.getCertificateType()));
		ks.setPassword(form.getCertPwd());
		ks.setAksId(UniqueSequenceGenerator.CHAR16.next());
		config.setKeyStore(ks);
		config.setSigningAlogrithm(RSASigningAlgorithm.fromValue(form.getSignAlgo()));
		return config;
	}

	@Override
	public List<SamlConfigurationVO> getAllSamlConfigs() throws AssuranceEntityException {
		List<SamlConfigurationVO> vos = new ArrayList<>();
		List<AssuranceSSOConfig> configs = this.configRepo.findAll();
		if (!CollectionUtils.isEmpty(configs)) {
			for (AssuranceSSOConfig asc : configs) {
				if (asc.getType() == SSOType.SAML) {
					MongoSSOConfigEntity mongoAsc = (MongoSSOConfigEntity) asc;
					SamlConfiguration samlConfig = (SamlConfiguration) asc.getMetadata();
					SamlConfigurationVO vo = new SamlConfigurationVO();
					vo.setActive(asc.isActive());
					vo.setConfigName(mongoAsc.getName());
					vo.setCertificateType(samlConfig.getKeyStore().getFormat().name());
					vo.setConfigId(asc.getId());
					vo.setEntityId(samlConfig.getEntityId());
					vo.setMetadata(samlConfig.getIdpMetadata());
					vo.setSignAlgo(samlConfig.getSigningAlogrithm().name());
					vos.add(vo);
				}
			}
		}
		return vos;
	}

	@Override
	public void deleteConfig(String configId) throws AssuranceEntityException {
		this.configRepo.delete(configId);
	}

	@Override
	public String saveOauthConfig(CreateOauthForm form) throws AssuranceEntityException {
		MongoSSOConfigEntity entity = null;
		if (StringUtils.isNotEmpty(form.getAddtoConfigId())) {
			Optional<MongoSSOConfigEntity> data = this.configRepo.findById(form.getAddtoConfigId());
			if (data.isPresent()) {
				entity = data.get();
			} else {
				throw new AssuranceEntityException("No configuration found with id [" + form.getAddtoConfigId() + "]");
			}
		} else {
			entity = this.createSSOConfig(SSOType.OAUTH, form.getConfigName());
			entity.setMetadata(new Oauth2Configuration());
		}
		Oauth2Configuration config = (Oauth2Configuration) entity.getMetadata();
		config.getRegistrations().add(this.createOauthClientRegister(form));
		return this.configRepo.save(entity);
	}

	protected OauthClientRegister createOauthClientRegister(CreateOauthForm form) {
		OauthClientRegister ocr = new OauthClientRegister();
		ocr.setAuthorizationUri(form.getAuthUri().strip());
		ocr.setClientId(form.getClientId().strip());
		ocr.setClientSecret(form.getClientSecret().strip());
		ocr.setJwkSetUri(form.getJwksUri().strip());
		ocr.setRegistrationId(form.getRegId().strip());
		ocr.setTokenUri(form.getTokenUri().strip());
		ocr.setUserInfoUri(form.getUserInfoUri().strip());
		return ocr;
	}

	@Override
	public List<OauthConfigurationVO> getAllOauthConfigs() throws AssuranceEntityException {
		return this.configRepo.findSSOConfigs(SSOType.OAUTH).stream().map(cfg -> {
			MongoSSOConfigEntity entity = (MongoSSOConfigEntity) cfg;
			return createOauthConfig(entity);
		}).collect(Collectors.toList());
	}

	protected OauthConfigurationVO createOauthConfig(MongoSSOConfigEntity entity) {
		OauthConfigurationVO vo = new OauthConfigurationVO();
		vo.setConfigId(entity.getId());
		vo.setConfigName(entity.getName());
		vo.setActive(entity.isActive());
		Oauth2Configuration config = (Oauth2Configuration) entity.getMetadata();
		vo.getClients().addAll(
				config.getRegistrations().stream().map(this::createClientRegistration).collect(Collectors.toList()));
		vo.setMetadata(this.clientsToJson(vo.getClients()));
		return vo;
	}

	protected String clientsToJson(List<ClientRegistrationVO> clients) {
		try {
			if (!CollectionUtils.isEmpty(clients)) {
				return MAPPER.writeValueAsString(clients);
			}
		} catch (Exception ex) {
		}
		return "[]";
	}

	protected ClientRegistrationVO createClientRegistration(OauthClientRegister ocr) {
		ClientRegistrationVO client = new ClientRegistrationVO();
		client.setAuthUri(ocr.getAuthorizationUri());
		client.setClientId(ocr.getClientId());
		client.setJwksUri(ocr.getJwkSetUri());
		client.setRegId(ocr.getRegistrationId());
		client.setTokenUri(ocr.getTokenUri());
		client.setUserInfoUri(ocr.getUserInfoUri());
		return client;
	}

	@Override
	public List<ViewAssuranceConfigVO> getAllConfigs() throws AssuranceEntityException {
		List<ViewAssuranceConfigVO> configs = new ArrayList<>();
		List<AssuranceSSOConfig> ssoConfigs = this.configRepo.findAll();
		if (!CollectionUtils.isEmpty(ssoConfigs)) {
			return ssoConfigs.stream().map(ViewAssuranceConfigVO::new).collect(Collectors.toList());
		}
		return configs;
	}

	@Override
	public void toggleActivation(String configId) throws AssuranceEntityException {
		Optional<MongoSSOConfigEntity> entity = this.configRepo.findById(configId);
		if (entity.isPresent()) {
			MongoSSOConfigEntity config = entity.get();
			if (config.isActive().booleanValue() && this.atleastOneConfigIsActiveOthethan(config)) {
				this.configRepo.update(configId, false);
			} else if (!config.isActive().booleanValue() && this.noConfigIsActiveForType(config.getType())) {
				this.configRepo.update(configId, true);
			} else {
				throw new AssuranceConfigurationException(
						"At least one configuration must be active and only one config of same SSOType is allowed");
			}
		} else {
			throw new AssuranceEntityException("Invalid config id");
		}
	}

	@Override
	public void resetConfig() throws AssuranceEntityException, ResetFailureException {
		try {
			this.asrConfigFactory.reset();
		} catch (ResetFailureException rfe) {
			LOGGER.error("Error resetting configuration ", rfe);
			LOGGER.error("Falling back to basic configuration ");
			this.resetToBasicAuthentication();
			throw new ResetFailureException("Unable to reset to said configuration, reset to basic instead");
		}
	}

	public AssuranceSSOConfig resetToBasicAuthentication() throws AssuranceEntityException {
		List<AssuranceSSOConfig> configs = this.configRepo.findAll();
		for (AssuranceSSOConfig config : configs) {
			this.configRepo.update(config.getId(), config.getType() == SSOType.BASIC);
		}
		return this.configRepo.findActiveSSOConfig(SSOType.BASIC);
	}

	protected boolean atleastOneConfigIsActiveOthethan(MongoSSOConfigEntity config) {
		List<MongoSSOConfigEntity> entities = this.configRepo.findAllActive();
		for (MongoSSOConfigEntity entity : entities) {
			if (!entity.getId().equals(config.getId())) {
				return true;
			}
		}
		return false;
	}

	protected boolean noConfigIsActiveForType(SSOType type) throws AssuranceEntityException {
		return this.configRepo.findActiveSSOConfig(type) == null;
	}

}
