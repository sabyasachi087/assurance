package com.sroyc.assurance.core;

import com.sroyc.assurance.core.exception.ResetFailureException;

@FunctionalInterface
public interface Resetable {

	public void reset() throws ResetFailureException;

}
