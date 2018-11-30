package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DNCTemplate {

	private int id;
	private String dncTemplateName;
	private String dncTemplateDesc;
	private String domain;
	private int type;
	private int status;
	private int serverType;
	private String serverAddr;
	private String creater;
	private String updater;
	private String updateTime;
	private String executeTime;
	private String filterCondition;
	private String importPath;
	private String importMode; 
	
	private int dncRosterNum;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getServerType() {
		return serverType;
	}

	public void setServerType(int serverType) {
		this.serverType = serverType;
	}

	public String getServerAddr() {
		return serverAddr;
	}

	public void setServerAddr(String serverAddr) {
		this.serverAddr = serverAddr;
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

	public String getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(String executeTime) {
		this.executeTime = executeTime;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public String getDncTemplateName() {
		return dncTemplateName;
	}

	public void setDncTemplateName(String dncTemplateName) {
		this.dncTemplateName = dncTemplateName;
	}

	public String getDncTemplateDesc() {
		return dncTemplateDesc;
	}

	public void setDncTemplateDesc(String dncTemplateDesc) {
		this.dncTemplateDesc = dncTemplateDesc;
	}

	public int getDncRosterNum() {
		return dncRosterNum;
	}

	public void setDncRosterNum(int dncRosterNum) {
		this.dncRosterNum = dncRosterNum;
	}

	public String getImportPath() {
		return importPath;
	}

	public void setImportPath(String importPath) {
		this.importPath = importPath;
	}

	public String getImportMode() {
		return importMode;
	}

	public void setImportMode(String importMode) {
		this.importMode = importMode;
	}
}
