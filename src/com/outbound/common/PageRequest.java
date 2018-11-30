package com.outbound.common;


import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.outbound.object.ActivityConditionModel;
import com.outbound.request.BaseRequest;

@XmlRootElement
public class PageRequest extends BaseRequest{

	private int startPage;
	private int pageNum;
	private String templateName; //名单模板名字
	private String activityName; //活动名字
	private String phoneNum;
	private String batchName;
	private String policyName; //策略名字
	private String activityBeginTime; //活动开始时间
	private String activityEndTime; //活动结束时间
	private String poolName;
	private String dncTemplateId;
	private String activityStatus; //活动状态  0 待执行  1： 执行中 , -1：暂停  ,-2 停止 ,3 完成
	private String rosterStatus; //名单状态 0 未呼叫 1 开始呼叫  2 呼叫结束 3禁呼
	private String domain;
	
	private String callRound;
	private String startTime;
	private String endTime;
	private String userName;
	
	private String ivrNum;//ivr号码
	
	private List<String> orderTypeList;
	private List<ActivityConditionModel> conditionInfoList;
	private int listType = 0; //1表示活动列表 

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityBeginTime() {
		return activityBeginTime;
	}

	public void setActivityBeginTime(String activityBeginTime) {
		this.activityBeginTime = activityBeginTime;
	}

	public String getActivityEndTime() {
		return activityEndTime;
	}

	public void setActivityEndTime(String activityEndTime) {
		this.activityEndTime = activityEndTime;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public String getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getDncTemplateId() {
		return dncTemplateId;
	}

	public void setDncTemplateId(String dncTemplateId) {
		this.dncTemplateId = dncTemplateId;
	}

	public String getRosterStatus() {
		return rosterStatus;
	}

	public void setRosterStatus(String rosterStatus) {
		this.rosterStatus = rosterStatus;
	}

	public String getCallRound() {
		return callRound;
	}

	public void setCallRound(String callRound) {
		this.callRound = callRound;
	}

	public List<String> getOrderTypeList() {
		return orderTypeList;
	}

	public void setOrderTypeList(List<String> orderTypeList) {
		this.orderTypeList = orderTypeList;
	}

	public int getListType() {
		return listType;
	}

	public void setListType(int listType) {
		this.listType = listType;
	}

	public List<ActivityConditionModel> getConditionInfoList() {
		return conditionInfoList;
	}

	public void setConditionInfoList(List<ActivityConditionModel> conditionInfoList) {
		this.conditionInfoList = conditionInfoList;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIvrNum() {
		return ivrNum;
	}

	public void setIvrNum(String ivrNum) {
		this.ivrNum = ivrNum;
	}
	

	
}

