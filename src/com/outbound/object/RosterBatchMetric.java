package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RosterBatchMetric {

	private int totalRosterBatchNum;
	private int finishedRosterBatchNum;
	private int todayRosterBatchNum;

	public int getTotalRosterBatchNum() {
		return totalRosterBatchNum;
	}

	public void setTotalRosterBatchNum(int totalRosterBatchNum) {
		this.totalRosterBatchNum = totalRosterBatchNum;
	}

	public int getFinishedRosterBatchNum() {
		return finishedRosterBatchNum;
	}

	public void setFinishedRosterBatchNum(int finishedRosterBatchNum) {
		this.finishedRosterBatchNum = finishedRosterBatchNum;
	}

	public int getTodayRosterBatchNum() {
		return todayRosterBatchNum;
	}

	public void setTodayRosterBatchNum(int todayRosterBatchNum) {
		this.todayRosterBatchNum = todayRosterBatchNum;
	}

}
