package com.sroyc.assurance.web.security;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.auth0.jwt.algorithms.Algorithm;
import com.sroyc.assurance.core.AssuranceTokenAlgorithmProvider;

@Component
public class JwtAlgorthm implements AssuranceTokenAlgorithmProvider {

	private static final Logger LOGGER = LogManager.getLogger(JwtAlgorthm.class);

	private final AtomicReference<Algorithm> algorithm = new AtomicReference<>();
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	@PostConstruct
	public void init() {
		this.generate();
		executor.scheduleAtFixedRate(this::generate, 1, 1, TimeUnit.DAYS);
	}

	@Override
	public Algorithm algorithm() {
		return algorithm.get();
	}

	private void generate() {
		this.algorithm.set(Algorithm.HMAC256(UUID.randomUUID().toString()));
		LOGGER.info("Algorithm has been reset");
	}

	@PreDestroy
	public void destroy() {
		this.executor.shutdown();
	}

}
