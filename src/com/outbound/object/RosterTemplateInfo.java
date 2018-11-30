package com.outbound.object;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RosterTemplateInfo {

	private int id;
	private String name;
	private String descb;
	private String domain;
	private List<DbColumn> dbcolumns; //条件信息
	private String columns;
	private String createtime;
	private String lastModifyTime;
	private String importPath;
	private String importMode;
	private String importTime;
	private String executeTime;
	private String lastImportTime;
	private String lastImportStatus;
	
	private int expireDays;
	private int contactNums;
	private String filterCondition;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescb() {
		return descb;
	}

	public void setDescb(String descb) {
		this.descb = descb;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
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

	public String getImportTime() {
		return importTime;
	}

	public void setImportTime(String importTime) {
		this.importTime = importTime;
	}

	public String getLastImportTime() {
		return lastImportTime;
	}

	public void setLastImportTime(String lastImportTime) {
		this.lastImportTime = lastImportTime;
	}

	public String getLastImportStatus() {
		return lastImportStatus;
	}

	public void setLastImportStatus(String lastImportStatus) {
		this.lastImportStatus = lastImportStatus;
	}

	public int getExpireDays() {
		return expireDays;
	}

	public void setExpireDays(int expireDays) {
		this.expireDays = expireDays;
	}

	public int getContactNums() {
		return contactNums;
	}

	public void setContactNums(int contactNums) {
		this.contactNums = contactNums;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}

	public List<DbColumn> getDbcolumns() {
		return dbcolumns;
	}

	public void setDbcolumns(List<DbColumn> dbcolumns) {
		this.dbcolumns = dbcolumns;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(String executeTime) {
		this.executeTime = executeTime;
	}
}
