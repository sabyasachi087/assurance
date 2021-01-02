package com.sroyc.assurance.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.sroyc.assurance.core.basic.DefaultBasicTokenAdapter;
import com.sroyc.assurance.core.config.AssuranceConfigurationProperty;
import com.sroyc.assurance.core.exception.ResetFailureException;
import com.sroyc.assurance.core.oauth.DefaultOAuthTokenAdapter;
import com.sroyc.assurance.core.saml.DefaultSamlTokenAdapter;
import com.sroyc.assurance.core.util.AssuranceCommonUtil;
import com.sroyc.assurance.core.util.SimpleJwtAlgorithm;

@Component
public class TokenAdapterFactory implements Resetable, InitializingBean {

	private static final Logger LOGGER = LogManager.getLogger(TokenAdapterFactory.class);

	private AssuranceConfigurationProperty config;
	private ApplicationContext context;
	private List<TokenAdapter> defaultAdapters = new ArrayList<>();
	private List<TokenAdapter> customAdapters = new ArrayList<>();

	private AssuranceTokenAlgorithmProvider algoProvider;

	@Autowired
	public TokenAdapterFactory(AssuranceConfigurationProperty config, ApplicationContext context) {
		super();
		this.config = config;
		this.context = context;
	}

	protected void resolveAlgoProvider() {
		this.algoProvider = AssuranceCommonUtil.getBeanByClass(AssuranceTokenAlgorithmProvider.class, this.context);
		if (this.algoProvider == null) {
			LOGGER.error("Unable to find bean of type [{}]. Falling back to default.",
					AssuranceTokenAlgorithmProvider.class.getCanonicalName());
			this.algoProvider = new SimpleJwtAlgorithm();
		}
	}

	protected void loadDefaultAdapters() {
		defaultAdapters.clear();
		this.defaultAdapters.add(new DefaultBasicTokenAdapter(this.config, this.algoProvider));
		this.defaultAdapters.add(new DefaultOAuthTokenAdapter(this.config, this.algoProvider));
		this.defaultAdapters.add(new DefaultSamlTokenAdapter(this.config, this.algoProvider));
	}

	protected void loadCustomeAdapters() {
		this.customAdapters.clear();
		Map<String, TokenAdapter> adapters = this.context.getBeansOfType(TokenAdapter.class);
		if (!CollectionUtils.isEmpty(adapters)) {
			adapters.values().forEach(this.customAdapters::add);
		}
	}

	@Override
	public void reset() throws ResetFailureException {
		this.resolveAlgoProvider();
		this.loadCustomeAdapters();
		this.loadDefaultAdapters();
	}

	public List<TokenAdapter> getDefaultAdapters() {
		return Collections.unmodifiableList(this.defaultAdapters);
	}

	public List<TokenAdapter> getCustomAdapters() {
		return Collections.unmodifiableList(this.customAdapters);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.reset();
	}

}
