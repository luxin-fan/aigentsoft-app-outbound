package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RosterInfoMetric {

	private int allRosterNum;
	private int todayAllRosterNum;
	private int todayOutCallNum;
	private int allCalledRosterNum;
	private int todayOutCallAnswerNum;

	public int getAllRosterNum() {
		return allRosterNum;
	}

	public void setAllRosterNum(int allRosterNum) {
		this.allRosterNum = allRosterNum;
	}

	public int getTodayAllRosterNum() {
		return todayAllRosterNum;
	}

	public void setTodayAllRosterNum(int todayAllRosterNum) {
		this.todayAllRosterNum = todayAllRosterNum;
	}

	public int getTodayOutCallNum() {
		return todayOutCallNum;
	}

	public void setTodayOutCallNum(int todayOutCallNum) {
		this.todayOutCallNum = todayOutCallNum;
	}

	public int getTodayOutCallAnswerNum() {
		return todayOutCallAnswerNum;
	}

	public void setTodayOutCallAnswerNum(int todayOutCallAnswerNum) {
		this.todayOutCallAnswerNum = todayOutCallAnswerNum;
	}

	public int getAllCalledRosterNum() {
		return allCalledRosterNum;
	}

	public void setAllCalledRosterNum(int allCalledRosterNum) {
		this.allCalledRosterNum = allCalledRosterNum;
	}

}
