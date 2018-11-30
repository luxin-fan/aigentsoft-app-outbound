package com.outbound.object;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import com.google.gson.reflect.TypeToken;
import com.outbound.object.util.GsonFactory;


@XmlRootElement
public class ActivityInfo {

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
	private List<ActivityConditionModel> conditionInfoList;
	private String uui;
	private String trunkGrp;
	private String lastUpdateTime;
	private String prefix;
	
	private String beginDatetime;
	private String endDatetime;

	private int activityExecuteType; //活动执行类型 0、普通任务 1、计划任务

	private String activityExecuteTime;
	private String activityBackTime;
	private String activityBackAddr;

	private int activityBackAddrType; //0手动 1模板导入 2自动导入
	private String orderType;
	private List<String> orderTypeList;
	private int completeType;
	
	private int rosterNum;
	private int batchNum;
	private int completeBatchNum;
	private int outCallNum;
	private int outCallRosterNum;
	private int unCallNum;
	private int answerCallNum;
	private int dncNum;
	
	private List<OutboundRecallPolicy> roundList;
	
	private String currentBatch;
	private int currentRound;
	private String outCallTimeNum;
	
	private int rosterNumDay;
	private int batchNumDay;
	private int outCallNumDay;
	private int answerCallNumDay;
	

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
		if (StringUtils.isNotBlank(orderType)){
			orderTypeList = GsonFactory.getGson().fromJson(orderType, new TypeToken<List<String>>(){}.getType());
		}
	}
	

	public void setOrderTypeList(List<String> orderTypeList) {
		this.orderTypeList = orderTypeList;
		if(!CollectionUtils.isEmpty(orderTypeList)){
			for (Iterator<String> it = orderTypeList.iterator(); it.hasNext();){
				String str = it.next();
				if (StringUtils.isBlank(str)){
					it.remove();
				}
			}
			if (CollectionUtils.isEmpty(orderTypeList)){return;}
			this.orderType = GsonFactory.getGson().toJson(orderTypeList);
		}
	}

	public List<String> getOrderTypeList() {
		return orderTypeList;
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
		if (StringUtils.isNotBlank(conditionInfo)){
			conditionInfoList = GsonFactory.getGson().fromJson(conditionInfo, new TypeToken<List<ActivityConditionModel>>(){}.getType());
		}
	}
	

	public List<ActivityConditionModel> getConditionInfoList() {
		return conditionInfoList;
	}
	

	public void setConditionInfoList(List<ActivityConditionModel> conditionInfoList) {
		this.conditionInfoList = conditionInfoList;
		if (!CollectionUtils.isEmpty(conditionInfoList)){
			for (Iterator<ActivityConditionModel> it = conditionInfoList.iterator(); it.hasNext();){
				ActivityConditionModel model = it.next();
				if (StringUtils.isBlank(model.getCondition())){
					it.remove();
				}
			}
			if (!CollectionUtils.isEmpty(conditionInfoList)){
				this.conditionInfo = GsonFactory.getBuildGson().toJson(conditionInfoList);
			}
		}
	}

	public String getRosterTemplateName() {
		return rosterTemplateName;
	}

	public void setRosterTemplateName(String rosterTemplateName) {
		this.rosterTemplateName = rosterTemplateName;
	}

	public int getRosterNum() {
		return rosterNum;
	}

	public void setRosterNum(int roterNum) {
		this.rosterNum = roterNum;
	}
	
	
	public void addRosterNum(int roterNum) {
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
		if(this.outCallNum > this.rosterNum){
			//this.rosterNum = this.outCallNum;
		}
	}
	
	public int getOutCallRosterNum() {
		return outCallRosterNum;
	}

	public void setOutCallRosterNum(int outCallRosterNum) {
		this.outCallRosterNum = outCallRosterNum;
	}
	
	public void addOutCallRosterNum(int outCallRosterNum){
		this.outCallRosterNum += outCallRosterNum;
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

	public int getBatchNum() {
		return batchNum;
	}
	
	public void addBatchNum() {
		this.batchNum ++;
	}

	public void setBatchNum(int batchNum) {
		this.batchNum = batchNum;
		if (this.batchNum < 0){
			this.batchNum = 0;
		}
	}

	public String getCurrentBatch() {
		return currentBatch;
	}

	public void setCurrentBatch(String currentBatch) {
		this.currentBatch = currentBatch;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	public List<OutboundRecallPolicy> getRoundList() {
		return roundList;
	}

	public void setRoundList(List<OutboundRecallPolicy> roundList) {
		this.roundList = roundList;
	}

	public String getOutCallTimeNum() {
		return outCallTimeNum;
	}

	public void setOutCallTimeNum(String outCallTimeNum) {
		this.outCallTimeNum = outCallTimeNum;
	}
	
	public int getRosterNumDay() {
		return rosterNumDay;
	}
	
	public void setRosterNumDay(int rosterNumDay) {
		this.rosterNumDay = rosterNumDay;
	}
	
	public void addRosterNumDay(int rosterNumDay) {
		this.rosterNumDay += rosterNumDay;
		if (this.rosterNumDay < 0){
			this.rosterNumDay = 0;
		}
	}
	
	public int getBatchNumDay() {
		return batchNumDay;
	}

	public void setBatchNumDay(int batchNumDay) {
		this.batchNumDay = batchNumDay;
	}
	
	public void addBatchNumDay() {
		this.batchNumDay ++;
	}
	
	public void subductionBatchNumDay() {
		if (batchNumDay > 0){
			this.batchNumDay --;
		}
	}

	public int getOutCallNumDay() {
		return outCallNumDay;
	}

	public void setOutCallNumDay(int outCallNumDay) {
		this.outCallNumDay = outCallNumDay;
	}
	
	public void addOutCallNumDay(int outCallNumDay) {
		this.outCallNumDay += outCallNumDay;
	}

	public int getAnswerCallNumDay() {
		return answerCallNumDay;
	}

	public void setAnswerCallNumDay(int answerCallNumDay) {
		this.answerCallNumDay = answerCallNumDay;
	}
	
	public void addAnswerCallNumDay(int answerCallNumDay) {
		this.answerCallNumDay += answerCallNumDay;
	}
	
	
}
