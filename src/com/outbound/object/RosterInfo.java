package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RosterInfo {

	private int id;
	private String jobId;
	private String phoneNum1;
	private String phoneNum2;
	private String phoneNum3;
	private String phoneNum4;
	private String phoneNum5;
	private String lastname;
	private String firstname;
	private int age;
	private String sex;
	private String customerId;
	private String address;

	private String email;
	private String cardType;
	private String cardNum;

	private String customFields;
	
	private String createTime;

	private int callRound = 1;
	
	private String callId;
	private String callee;
	private String makeCallTime;
	private String callAnswerTime;
	private String callEndTime;
	private String callResult;
	private int callTime;
	private int answerCallTime;
	private int resultCode = -1;

	// 0 未呼叫 1 开始呼叫 2 呼叫结束
	private int status;
	private String recordPath;
	private int isRecord;
	private String batchName;
	private String templateName;
	private String activityName;
	private String domain;
	private String classification = "0";
	// 名单是否回调
	private int isrecall = 0;
	// 第几次外呼
	private int callOutTimes = 0;
	private String makeCallNum;
	private String currentCallNum;
	private String trunkGroup;
	private String ani;
	private String uui;
	private int rosterId;
	// 0 未导出 1 已导出
	private int isExport;

	public int getRosterId() {
		return rosterId;
	}

	public void setRosterId(int rosterId) {
		this.rosterId = rosterId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPhoneNum1() {
		return phoneNum1;
	}

	public void setPhoneNum1(String phoneNum1) {
		this.phoneNum1 = phoneNum1;
	}

	public String getPhoneNum2() {
		return phoneNum2;
	}

	public void setPhoneNum2(String phoneNum2) {
		this.phoneNum2 = phoneNum2;
	}

	public String getPhoneNum3() {
		return phoneNum3;
	}

	public void setPhoneNum3(String phoneNum3) {
		this.phoneNum3 = phoneNum3;
	}

	public String getPhoneNum4() {
		return phoneNum4;
	}

	public void setPhoneNum4(String phoneNum4) {
		this.phoneNum4 = phoneNum4;
	}

	public String getPhoneNum5() {
		return phoneNum5;
	}

	public void setPhoneNum5(String phoneNum5) {
		this.phoneNum5 = phoneNum5;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public String getCustomFields() {
		return customFields;
	}

	public void setCustomFields(String customFields) {
		this.customFields = customFields;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public String getCallee() {
		return callee;
	}

	public void setCallee(String callee) {
		this.callee = callee;
	}

	public String getMakeCallTime() {
		return makeCallTime;
	}

	public void setMakeCallTime(String makeCallTime) {
		this.makeCallTime = makeCallTime;
	}

	public String getCallEndTime() {
		return callEndTime;
	}

	public void setCallEndTime(String callEndTime) {
		this.callEndTime = callEndTime;
	}

	public String getCallResult() {
		return callResult;
	}

	public void setCallResult(String callResult) {
		this.callResult = callResult;
	}

	public int getCallTime() {
		return callTime;
	}

	public void setCallTime(int callTime) {
		this.callTime = callTime;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public int getCallRound() {
		return callRound;
	}

	public void setCallRound(int callRound) {
		this.callRound = callRound;
	}

	public String getCallAnswerTime() {
		return callAnswerTime;
	}

	public void setCallAnswerTime(String callAnswerTime) {
		this.callAnswerTime = callAnswerTime;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public int getIsrecall() {
		return isrecall;
	}

	public void setIsrecall(int isrecall) {
		this.isrecall = isrecall;
	}

	public int getAnswerCallTime() {
		return answerCallTime;
	}

	public void setAnswerCallTime(int answerCallTime) {
		this.answerCallTime = answerCallTime;
	}

	public String getRecordPath() {
		return recordPath;
	}

	public void setRecordPath(String recordPath) {
		this.recordPath = recordPath;
	}

	public int getIsRecord() {
		return isRecord;
	}

	public void setIsRecord(int isRecord) {
		this.isRecord = isRecord;
	}

	public int getCallOutTimes() {
		return callOutTimes;
	}

	public void setCallOutTimes(int callOutTimes) {
		this.callOutTimes = callOutTimes;
	}

	public String getMakeCallNum() {
		return makeCallNum;
	}

	public void setMakeCallNum(String makeCallNum) {
		this.makeCallNum = makeCallNum;
	}

	public String getTrunkGroup() {
		return trunkGroup;
	}

	public void setTrunkGroup(String trunkGroup) {
		this.trunkGroup = trunkGroup;
	}

	public String getAni() {
		return ani;
	}

	public void setAni(String ani) {
		this.ani = ani;
	}

	public String getUui() {
		return uui;
	}

	public void setUui(String uui) {
		this.uui = uui;
	}

	public String getCurrentCallNum() {
		return currentCallNum;
	}

	public void setCurrentCallNum(String currentCallNum) {
		this.currentCallNum = currentCallNum;
	}
	
	public int getIsExport() {
		return isExport;
	}

	public void setIsExport(int isExport) {
		this.isExport = isExport;
	}
}
