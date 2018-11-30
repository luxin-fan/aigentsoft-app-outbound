package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

/*
 * add by potti at 2018-09-11 15:50
 */
@XmlRootElement
public class Roster {
	private int id;
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
	private int status; // 0 未外呼 1 已被系统拉取但是未外呼 2 已开始发起外呼 3 完成 4人为干预禁止外呼
	private String batchName;
	private String templateName;
	private String activityName;
	private String domain;
	private int callRound; // 当前已执行的轮次
	private int reCall; // 当前已执行的回呼次数
	private String createTime;
	private String makeCallTime; // 外呼时间

	private String currentCallNum;

	public String getCurrentCallNum() {
		return currentCallNum;
	}

	public void setCurrentCallNum(String currentCallNum) {
		this.currentCallNum = currentCallNum;
	}

	public int getCallRound() {
		return callRound;
	}

	public void setCallRound(int callRound) {
		this.callRound = callRound;
	}

	public int getReCall() {
		return reCall;
	}

	public void setReCall(int reCall) {
		this.reCall = reCall;
	}

	public String getMakeCallTime() {
		return makeCallTime;
	}

	public void setMakeCallTime(String makeCallTime) {
		this.makeCallTime = makeCallTime;
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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
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

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityName == null) ? 0 : activityName.hashCode());
		result = prime * result + ((batchName == null) ? 0 : batchName.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Roster other = (Roster) obj;
		if (activityName == null) {
			if (other.activityName != null)
				return false;
		} else if (!activityName.equals(other.activityName))
			return false;
		if (batchName == null) {
			if (other.batchName != null)
				return false;
		} else if (!batchName.equals(other.batchName))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
}
