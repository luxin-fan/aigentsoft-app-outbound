package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MonitorHistory {

	private int id;
	private String monitorTime;
	private int monitorCount;
	private int monitorType;
	private int status;
	private String creater;
	private String updater;
	private String updateTime;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMonitorTime() {
		return monitorTime;
	}

	public void setMonitorTime(String monitorTime) {
		this.monitorTime = monitorTime;
	}

	public int getMonitorCount() {
		return monitorCount;
	}

	public void setMonitorCount(int monitorCount) {
		this.monitorCount = monitorCount;
	}

	public int getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(int monitorType) {
		this.monitorType = monitorType;
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

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

}
