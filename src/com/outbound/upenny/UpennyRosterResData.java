package com.outbound.upenny;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UpennyRosterResData {
	
	private String receivedTime;

	public UpennyRosterResData() {
		receivedTime  = ""+ System.currentTimeMillis();
	}

	public String getReceivedTime() {
		return receivedTime;
	}

	public void setReceivedTime(String receivedTime) {
		this.receivedTime = receivedTime;
	}
}
