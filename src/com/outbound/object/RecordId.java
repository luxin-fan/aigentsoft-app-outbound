package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecordId {

	private int id;
	private String domain;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


}
