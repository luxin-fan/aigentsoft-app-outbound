package com.outbound.impl.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ReqResponse {

	private int code;
	private String errMsg;
	private String callId;

	public String toString() {
		return "code:" + code + " | " + "errMsg:" + errMsg + " | " + "callId:" + callId;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

}
