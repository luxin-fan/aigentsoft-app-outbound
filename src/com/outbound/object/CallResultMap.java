package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CallResultMap {

	private int id;
	private int resultCode;
	private String resultDesc;
	// 0 open 1 inwork 2 completed
	private String resultMap;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultDesc() {
		return resultDesc;
	}

	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}

	public String getResultMap() {
		return resultMap;
	}

	public void setResultMap(String resultMap) {
		this.resultMap = resultMap;
	}

}
