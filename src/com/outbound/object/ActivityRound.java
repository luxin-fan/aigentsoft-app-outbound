package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActivityRound {

	private int id;
	private String activityinfoName;
	private String callResultType;
	// 0 open 1 inwork 2 completed
	private String phoneNumCloumn;
	private String creater;
	private String updater;

	private int callTurnInterval;
	private int callTurnSort;
	private int status;
	private String updateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getActivityinfoName() {
		return activityinfoName;
	}

	public void setActivityinfoName(String activityinfoName) {
		this.activityinfoName = activityinfoName;
	}

	public String getCallResultType() {
		return callResultType;
	}

	public void setCallResultType(String callResultType) {
		this.callResultType = callResultType;
	}

	public String getPhoneNumCloumn() {
		return phoneNumCloumn;
	}

	public void setPhoneNumCloumn(String phoneNumCloumn) {
		this.phoneNumCloumn = phoneNumCloumn;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public int getCallTurnInterval() {
		return callTurnInterval;
	}

	public void setCallTurnInterval(int callTurnInterval) {
		this.callTurnInterval = callTurnInterval;
	}

	public int getCallTurnSort() {
		return callTurnSort;
	}

	public void setCallTurnSort(int callTurnSort) {
		this.callTurnSort = callTurnSort;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
}
