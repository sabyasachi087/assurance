package com.sroyc.assurance.web.view.model;

import java.io.Serializable;

public class IdName implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 806617716858923015L;

	private String id;
	private String name;

	public IdName() {
		super();
	}

	public IdName(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
