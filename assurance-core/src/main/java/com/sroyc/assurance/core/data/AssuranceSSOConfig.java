package com.sroyc.assurance.core.data;

import java.io.Serializable;

public class AssuranceSSOConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7873814211326607700L;

	private String id;
	private SSOType type;
	private String name;
	private Boolean active;
	private AssuranceMetadata metadata;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SSOType getType() {
		return type;
	}

	public void setType(SSOType type) {
		this.type = type;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public AssuranceMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(AssuranceMetadata metadata) {
		this.metadata = metadata;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
