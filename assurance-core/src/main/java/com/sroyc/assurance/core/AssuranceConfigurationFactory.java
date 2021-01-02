package com.sroyc.assurance.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import com.sroyc.assurance.core.exception.AssuranceConfigurationException;
import com.sroyc.assurance.core.exception.ResetFailureException;
import com.sroyc.assurance.core.sso.AssuranceSuccessHandler;
import com.sroyc.assurance.core.sso.SSOConfiguration;

@Component
public class AssuranceConfigurationFactory implements Resetable, ApplicationListener<ContextRefreshedEvent> {

	private ApplicationContext ctx;
	private FilterChainProxy filter;
	private Filter channelProcessingFilter;
	private List<SSOConfiguration> activeConfigs = new ArrayList<>();

	@Autowired
	public AssuranceConfigurationFactory(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	protected void loadConfig() throws ResetFailureException {
		if (this.filter == null) {
			Map<String, SSOConfiguration> configMaps = this.ctx.getBeansOfType(SSOConfiguration.class);
			for (SSOConfiguration config : configMaps.values()) {
				config.reset();
				if (config.isActive()) {
					activeConfigs.add(config);
				}
			}
		}
	}

	protected void loadChannelProcessingFilter() {
		for (SSOConfiguration config : this.activeConfigs) {
			if (config.channelProcessingFilter() != null) {
				this.channelProcessingFilter = config.channelProcessingFilter();
				return;
			}
		}
	}

	public List<SSOConfiguration> getConfigurations() {
		try {
			return this.activeConfigs;
		} catch (Exception ex) {
			throw new AssuranceConfigurationException(ex);
		}
	}

	public Filter filter() {
		try {
			return this.filter;
		} catch (Exception ex) {
			throw new AssuranceConfigurationException(ex);
		}
	}

	protected void loadFilter() {
		List<SecurityFilterChain> chains = new ArrayList<>();
		for (SSOConfiguration config : this.activeConfigs) {
			chains.add(new DefaultSecurityFilterChain(config.matcher(), config.filter()));
		}
		this.filter = new FilterChainProxy(chains);
	}

	@Override
	public void reset() throws ResetFailureException {
		try {
			this.filter = null;
			this.activeConfigs.clear();
			this.ctx.getBean(AssuranceSuccessHandler.class).reset();
			this.ctx.getBean(AssuranceTokenFilter.class).reset();
			this.loadConfig();
			this.loadFilter();
			this.loadChannelProcessingFilter();
		} catch (Exception ex) {
			throw new ResetFailureException(ex);
		}
	}

	public Filter getChannelProcessingFilter() {
		return channelProcessingFilter;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			this.reset();
		} catch (ResetFailureException e) {
			throw new AssuranceConfigurationException(e);
		}
	}

}
