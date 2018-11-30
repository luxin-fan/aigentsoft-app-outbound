package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DNCNumber {

	private int id;
	private String phoneNum;
	private String domain;
	
	private String dncTemplateId;
	private int status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDncTemplateId() {
		return dncTemplateId;
	}

	public void setDncTemplateId(String dncTemplateId) {
		this.dncTemplateId = dncTemplateId;
	}
}
