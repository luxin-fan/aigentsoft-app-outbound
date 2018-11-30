package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActivityCompleteConfig {

	private int id;
	private String name;
	private String completeCondition;
	// 0 open 1 inwork 2 completed
	private int status;
	private String completeSymbol;
	private String completeContent;
	private String creater;
	private String updater;
	private String updateTime;
	private String domain;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCompleteCondition() {
		return completeCondition;
	}

	public void setCompleteCondition(String completeCondition) {
		this.completeCondition = completeCondition;
	}

	public String getCompleteSymbol() {
		return completeSymbol;
	}

	public void setCompleteSymbol(String completeSymbol) {
		this.completeSymbol = completeSymbol;
	}

	public String getCompleteContent() {
		return completeContent;
	}

	public void setCompleteContent(String completeContent) {
		this.completeContent = completeContent;
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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
