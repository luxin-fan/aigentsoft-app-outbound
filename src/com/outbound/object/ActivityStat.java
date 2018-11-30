package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ActivityStat {

	private int id;
	private String activityName;
	private String rosterName;
	// 0 open 1 inwork 2 completed
	private String state;
	private String starttime;
	private String endtime;

	private int rosterNums;
	private int outreachRosterNums;
	private int establshRosterNums;
	private int totalCalls;
	private int establishCalls;
	private int validCalls;
	private int deliverIvrCalls;
	private int deliverAgentCalls;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getRosterName() {
		return rosterName;
	}

	public void setRosterName(String rosterName) {
		this.rosterName = rosterName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public int getRosterNums() {
		return rosterNums;
	}

	public void setRosterNums(int rosterNums) {
		this.rosterNums = rosterNums;
	}

	public int getOutreachRosterNums() {
		return outreachRosterNums;
	}

	public void setOutreachRosterNums(int outreachRosterNums) {
		this.outreachRosterNums = outreachRosterNums;
	}

	public int getEstablshRosterNums() {
		return establshRosterNums;
	}

	public void setEstablshRosterNums(int establshRosterNums) {
		this.establshRosterNums = establshRosterNums;
	}

	public int getTotalCalls() {
		return totalCalls;
	}

	public void setTotalCalls(int totalCalls) {
		this.totalCalls = totalCalls;
	}

	public int getEstablishCalls() {
		return establishCalls;
	}

	public void setEstablishCalls(int establishCalls) {
		this.establishCalls = establishCalls;
	}

	public int getValidCalls() {
		return validCalls;
	}

	public void setValidCalls(int validCalls) {
		this.validCalls = validCalls;
	}

	public int getDeliverIvrCalls() {
		return deliverIvrCalls;
	}

	public void setDeliverIvrCalls(int deliverIvrCalls) {
		this.deliverIvrCalls = deliverIvrCalls;
	}

	public int getDeliverAgentCalls() {
		return deliverAgentCalls;
	}

	public void setDeliverAgentCalls(int deliverAgentCalls) {
		this.deliverAgentCalls = deliverAgentCalls;
	}

}
