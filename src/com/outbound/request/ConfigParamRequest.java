package com.outbound.request;

import javax.xml.bind.annotation.XmlRootElement;

import com.outbound.common.PageRequest;

@XmlRootElement
public class ConfigParamRequest extends PageRequest {
	private String paramType;

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	
}
