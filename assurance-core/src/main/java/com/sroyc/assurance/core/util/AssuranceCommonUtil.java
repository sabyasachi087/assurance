package com.sroyc.assurance.core.util;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class AssuranceCommonUtil {

	private AssuranceCommonUtil() {
	}

	/**
	 * Find singleton bean for the matching type or returns null if none or more
	 * than one found
	 */
	public static final <T> T getBeanByClass(Class<T> clazz, ApplicationContext context) {
		try {
			return context.getBean(clazz);
		} catch (NoSuchBeanDefinitionException nsdbe) {
		}
		return null;
	}

}
