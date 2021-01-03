package com.sroyc.assurance.web.util;

import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sroyc.assurance.web.exception.SequenceGenerationException;

public enum UniqueSequenceGenerator {

	CHAR16(16), CHAR24(24);

	private Integer bits;
	private static final Integer MAX_QUEUE_SIZE = 1024;
	private BlockingQueue<String> sequences = new ArrayBlockingQueue<>(1024);
	private static final ThreadPoolExecutor CTRL = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	private static final AtomicBoolean IS_LOADING = new AtomicBoolean(false);
	private static final Logger LOGGER = LogManager.getLogger(UniqueSequenceGenerator.class);

	private UniqueSequenceGenerator(Integer bits) {
		this.bits = bits;
	}

	public String next() {
		try {
			if (sequences.size() < 100 && !IS_LOADING.get()) {
				exec();
			}
			String seq = sequences.poll(1, TimeUnit.SECONDS);
			return seq == null ? generate() : seq;
		} catch (Exception ex) {
			throw new SequenceGenerationException(ex);
		}
	}

	private String generate() {
		StringBuilder sb = new StringBuilder();
		LocalDateTime ldt = LocalDateTime.now();
		sb.append(ldt.getYear()).append(RandomStringUtils.randomAlphabetic(2)).append(ldt.getMonthValue())
				.append(RandomStringUtils.randomAlphabetic(2)).append(ldt.getDayOfMonth())
				.append(RandomStringUtils.randomAlphanumeric(bits - 12));
		return sb.toString().toUpperCase();
	}

	private static void exec() {
		if (CTRL.getActiveCount() == 0) {
			CTRL.execute(() -> {
				for (UniqueSequenceGenerator generator : values()) {
					loadQueues(generator);
				}
			});
		}
	}

	private static void loadQueues(UniqueSequenceGenerator generator) {
		try {
			IS_LOADING.set(true);
			while (generator.sequences.size() < MAX_QUEUE_SIZE) {
				if (!generator.sequences.offer(generator.generate(), 1, TimeUnit.SECONDS)) {
					LOGGER.info("Sequence is full , ignoring load ... ");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error loading sequence queues ", e);
		} finally {
			IS_LOADING.set(false);
		}
	}

}
