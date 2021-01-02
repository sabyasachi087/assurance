package com.sroyc.assurance.web;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sroyc.assurance.web.util.UniqueSequenceGenerator;

class TestGeneral {

	@Test
	void testSequence() throws Exception {
		ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
		Integer uniqueSize = 10000;
		final Set<String> char16 = new HashSet<>();
		final Set<String> char24 = new HashSet<>();
		exec.execute(() -> {
			for (int i = 0; i < uniqueSize; i++) {
				String ch16 = UniqueSequenceGenerator.CHAR16.next();
				String ch24 = UniqueSequenceGenerator.CHAR24.next();
				Assertions.assertEquals(16, ch16.length());
				Assertions.assertEquals(24, ch24.length());
				char16.add(ch16);
				char24.add(ch24);
			}
		});
		exec.shutdown();
		exec.awaitTermination(1, TimeUnit.MINUTES);
		Assertions.assertEquals(uniqueSize, char16.size());
		Assertions.assertEquals(uniqueSize, char24.size());
	}

}
