package com.outbound.common;


import javax.xml.bind.annotation.XmlRootElement;

import com.outbound.request.BaseRequest;

@XmlRootElement
public class CheckRequest extends BaseRequest{

	private String name; //名单模板名字
	private String domain;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

