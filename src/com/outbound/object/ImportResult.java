package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ImportResult {

	private String result;//suc fail
	private int sucRows;
	
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public int getSucRows() {
		return sucRows;
	}
	public void setSucRows(int sucRows) {
		this.sucRows = sucRows;
	}
}
