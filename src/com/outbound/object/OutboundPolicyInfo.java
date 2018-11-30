package com.outbound.object;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;


@XmlRootElement
public class OutboundPolicyInfo {

	private int id;
	private String name;
	private String domain;
	private String descb;
	private List<String> timeRange;
	private String timeRangeStr;
	private List<String> callResultList;
	private String resProcessStr;

	private int policyType;
	private int workDayType;
	
	private String callAnswerStep;
	

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

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDescb() {
		return descb;
	}

	public void setDescb(String descb) {
		this.descb = descb;
	}


	public int getPolicyType() {
		return policyType;
	}

	public void setPolicyType(int policyType) {
		this.policyType = policyType;
	}

	public int getWorkDayType() {
		return workDayType;
	}

	public void setWorkDayType(int workDayType) {
		this.workDayType = workDayType;
	}

	public String getCallAnswerStep() {
		return callAnswerStep;
	}

	public void setCallAnswerStep(String callAnswerStep) {
		this.callAnswerStep = callAnswerStep;
	}

	public String getTimeRangeStr() {
		return timeRangeStr;
	}

	public void setTimeRangeStr(String timeRangeStr) {
		this.timeRangeStr = timeRangeStr;
		if (StringUtils.isBlank(timeRangeStr)) return;
		String[] timeRangeArr = timeRangeStr.split("[,]");
		List<String> timeRangeList = new ArrayList<String>();
		for (String timeRange : timeRangeArr) {
			timeRange = timeRange.replace("[", "");
			timeRange = timeRange.replace("]", "");
			timeRange = timeRange.replace("\"", "");
			timeRangeList.add(timeRange);
		}
		this.setTimeRange(timeRangeList);
	}

	public String getResProcessStr() {
		return resProcessStr;
	}

	public void setResProcessStr(String resProcessStr) {
		this.resProcessStr = resProcessStr;
	}

	public List<String> getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(List<String> timeRange) {
		this.timeRange = timeRange;
	}

	public List<String> getCallResultList() {
		return callResultList;
	}

	public void setCallResultList(List<String> callResultList) {
		this.callResultList = callResultList;
	}
	
	public void genCallRoundList(){
		this.callResultList = new ArrayList<String>();
		String[] resProcessArr = resProcessStr.split("[,]");
		for(String resp : resProcessArr){
			resp = resp.replace("[", "");
			resp = resp.replace("]", "");
			resp = resp.replace("\"", "");
			callResultList.add(resp);
		}
	}
}
