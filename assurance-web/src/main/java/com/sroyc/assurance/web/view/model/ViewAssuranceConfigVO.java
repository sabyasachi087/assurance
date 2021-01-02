package com.sroyc.assurance.web.view.model;

import java.io.Serializable;

import com.sroyc.assurance.core.data.AssuranceSSOConfig;

public class ViewAssuranceConfigVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3936882745039220395L;

	private String id;
	private String name;
	private String type;
	private boolean active;

	public ViewAssuranceConfigVO(AssuranceSSOConfig config) {
		this.id = config.getId();
		this.name = config.getName();
		this.type = config.getType().name();
		this.active = config.isActive();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public boolean isActive() {
		return active;
	}

}
