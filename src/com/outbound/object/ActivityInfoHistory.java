package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActivityInfoHistory {

	private int id;
	private String name;
	private String domain;
	
	//0 待执行 1 正在执行  2 暂停 3 已停止
	private int status;
	private String region;
	private String descb;
	private String policyName;
	private String rosterTemplateName;

	private int maxCapacity;
	private int priority;

	private String localNo;
	private String conditionInfo;
	private String uui;
	private String trunkGrp;
	private String lastUpdateTime;
	private String prefix;
	
	private String beginDatetime;
	private String endDatetime;

	private int activityExecuteType;

	private String activityExecuteTime;
	private String activityBackTime;
	private String activityBackAddr;

	private int activityBackAddrType;
	private String orderType;
	private int completeType;
	
	private int rosterNum;
	private int completeBatchNum;
	private int outCallNum;
	private int unCallNum;
	private int answerCallNum;
	private int dncNum;

	private int batchNum;
	private int outCallRosterNum;
	private String outCallTimeNum;
	
	
	public int getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(int batchNum) {
		this.batchNum = batchNum;
	}

	public int getOutCallRosterNum() {
		return outCallRosterNum;
	}

	public void setOutCallRosterNum(int outCallRosterNum) {
		this.outCallRosterNum = outCallRosterNum;
	}

	public String getOutCallTimeNum() {
		return outCallTimeNum;
	}

	public void setOutCallTimeNum(String outCallTimeNum) {
		this.outCallTimeNum = outCallTimeNum;
	}

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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getLocalNo() {
		return localNo;
	}

	public void setLocalNo(String localNo) {
		this.localNo = localNo;
	}

	public String getUui() {
		return uui;
	}

	public void setUui(String uui) {
		this.uui = uui;
	}

	public String getTrunkGrp() {
		return trunkGrp;
	}

	public void setTrunkGrp(String trunkGrp) {
		this.trunkGrp = trunkGrp;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getBeginDatetime() {
		return beginDatetime;
	}

	public void setBeginDatetime(String beginDatetime) {
		this.beginDatetime = beginDatetime;
	}

	public String getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(String endDatetime) {
		this.endDatetime = endDatetime;
	}

	public int getActivityExecuteType() {
		return activityExecuteType;
	}

	public void setActivityExecuteType(int activityExecuteType) {
		this.activityExecuteType = activityExecuteType;
	}

	public String getActivityExecuteTime() {
		return activityExecuteTime;
	}

	public void setActivityExecuteTime(String activityExecuteTime) {
		this.activityExecuteTime = activityExecuteTime;
	}

	public String getActivityBackTime() {
		return activityBackTime;
	}

	public void setActivityBackTime(String activityBackTime) {
		this.activityBackTime = activityBackTime;
	}

	public String getActivityBackAddr() {
		return activityBackAddr;
	}

	public void setActivityBackAddr(String activityBackAddr) {
		this.activityBackAddr = activityBackAddr;
	}

	public int getActivityBackAddrType() {
		return activityBackAddrType;
	}

	public void setActivityBackAddrType(int activityBackAddrType) {
		this.activityBackAddrType = activityBackAddrType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public int getCompleteType() {
		return completeType;
	}

	public void setCompleteType(int completeType) {
		this.completeType = completeType;
	}

	public String getDescb() {
		return descb;
	}

	public void setDescb(String descb) {
		this.descb = descb;
	}

	public String getConditionInfo() {
		return conditionInfo;
	}

	public void setConditionInfo(String conditionInfo) {
		this.conditionInfo = conditionInfo;
	}

	public String getRosterTemplateName() {
		return rosterTemplateName;
	}

	public void setRosterTemplateName(String rosterTemplateName) {
		this.rosterTemplateName = rosterTemplateName;
	}

	public void addRotserNum(int roterNum) {
		this.rosterNum += roterNum;
	}

	public int getCompleteBatchNum() {
		return completeBatchNum;
	}

	public void setCompleteBatchNum(int completeBatchNum) {
		this.completeBatchNum = completeBatchNum;
	}
	
	public void addCompleteBatchNum() {
		this.completeBatchNum ++;
	}

	public int getOutCallNum() {
		return outCallNum;
	}

	public void setOutCallNum(int outCallNum) {
		this.outCallNum = outCallNum;
	}
	
	public void addOutCallNum(int outCallNum) {
		this.outCallNum += outCallNum;
	}

	public int getUnCallNum() {
		return unCallNum;
	}

	public void setUnCallNum(int unCallNum) {
		this.unCallNum = unCallNum;
	}
	
	public void adUnCallNum(int unCallNum) {
		this.unCallNum += unCallNum;
	}

	public int getAnswerCallNum() {
		return answerCallNum;
	}

	public void setAnswerCallNum(int answerCallNum) {
		this.answerCallNum = answerCallNum;
	}
	
	public void addAnswerCallNum(int answerCallNum) {
		this.answerCallNum += answerCallNum;
	}

	public int getDncNum() {
		return dncNum;
	}

	public void setDncNum(int dncNum) {
		this.dncNum = dncNum;
	}
	
	public void addDncNum(int dncNum) {
		this.dncNum += dncNum;
	}

	public int getRosterNum() {
		return rosterNum;
	}

	public void setRosterNum(int rosterNum) {
		this.rosterNum = rosterNum;
	}

}
