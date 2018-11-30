package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RosterMonitorLog {

	private int id;
	private String rosterName;
	private String rosterBatchId;
	private int rosterTurn;
	private int rosterMonitorCount;
	private int rosterMonitorType;
	private int status;
	private String creater;
	private String updater;
	private String createTime;
	private String updateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRosterName() {
		return rosterName;
	}

	public void setRosterName(String rosterName) {
		this.rosterName = rosterName;
	}

	public String getRosterBatchId() {
		return rosterBatchId;
	}

	public void setRosterBatchId(String rosterBatchId) {
		this.rosterBatchId = rosterBatchId;
	}

	public int getRosterTurn() {
		return rosterTurn;
	}

	public void setRosterTurn(int rosterTurn) {
		this.rosterTurn = rosterTurn;
	}

	public int getRosterMonitorCount() {
		return rosterMonitorCount;
	}

	public void setRosterMonitorCount(int rosterMonitorCount) {
		this.rosterMonitorCount = rosterMonitorCount;
	}

	public int getRosterMonitorType() {
		return rosterMonitorType;
	}

	public void setRosterMonitorType(int rosterMonitorType) {
		this.rosterMonitorType = rosterMonitorType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
}
