package com.outbound.impl.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BatchResponse {

	private String msg;
	private String error;
	private String status;

	public String toString() {
		return "msg:" + msg + " | " + "error:" + error + " | " + "status:" + status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
