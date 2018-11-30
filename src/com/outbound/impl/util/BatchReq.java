package com.outbound.impl.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BatchReq {

	private String type;
	private String batchId;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

}
