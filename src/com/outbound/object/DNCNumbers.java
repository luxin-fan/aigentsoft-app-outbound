package com.outbound.object;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DNCNumbers {

	private int id;
	private List<String> phoneNumList;
	private String domain;
	private String dncTemplateId;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDncTemplateId() {
		return dncTemplateId;
	}

	public void setDncTemplateId(String dncTemplateId) {
		this.dncTemplateId = dncTemplateId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<String> getPhoneNumList() {
		return phoneNumList;
	}

	public void setPhoneNumList(List<String> phoneNumList) {
		this.phoneNumList = phoneNumList;
	}
}
