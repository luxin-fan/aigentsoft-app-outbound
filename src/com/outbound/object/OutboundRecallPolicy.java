package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OutboundRecallPolicy {

	private int id;
	private String activityName;
	private int round;
	private String callResult;
	private String phoneNumCol;
	private int callInterval;
	private String processInfo;
	private String domain;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCallResult() {
		return callResult;
	}

	public void setCallResult(String callResult) {
		this.callResult = callResult;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public String getProcessInfo() {
		return processInfo;
	}

	public void setProcessInfo(String processInfo) {
		this.processInfo = processInfo;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getCallInterval() {
		return callInterval;
	}

	public void setCallInterval(int callInterval) {
		this.callInterval = callInterval;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getPhoneNumCol() {
		return phoneNumCol;
	}

	public void setPhoneNumCol(String phoneNumCol) {
		this.phoneNumCol = phoneNumCol;
	}

}
