package com.sroyc.assurance.core.sso;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.sroyc.assurance.core.Resetable;
import com.sroyc.assurance.core.data.SSOType;
import com.sroyc.assurance.core.exception.AssuranceRuntimeException;

@Component
public class AssuranceProviderManager implements AuthenticationManager, Resetable {

	private static final Logger LOGGER = LogManager.getLogger(AssuranceProviderManager.class);

	private Map<SSOType, AuthenticationProvider> authStore = new EnumMap<>(SSOType.class);
	private ProviderManager provider = null;
	private ReentrantLock lock = new ReentrantLock();

	public void add(SSOType key, AuthenticationProvider provider) {
		this.authStore.put(key, provider);
		this.reloadProvider();
	}

	public void remove(SSOType key) {
		this.authStore.remove(key);
		this.reloadProvider();
	}

	private void reloadProvider() {
		try {
			this.lock.lock();
			if (!CollectionUtils.isEmpty(this.authStore.values())) {
				this.provider = new ProviderManager(new ArrayList<>(this.authStore.values()));
			}
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		this.waitForRelease();
		if (CollectionUtils.isEmpty(this.authStore)) {
			throw new InsufficientAuthenticationException("Not a single authentication provider is available");
		}
		return this.provider.authenticate(authentication);
	}

	public void waitForRelease() {
		while (this.lock.isLocked()) {
			try {
				LOGGER.warn("Auth provider is locked, waiting ... ");
				TimeUnit.SECONDS.sleep(1);
			} catch (Exception ex) {
				LOGGER.error("Thread interrupted {}", ex.getMessage());
				throw new AssuranceRuntimeException(ex);
			}
		}
	}

	@Override
	public void reset() {
		this.authStore.clear();
	}

}
