package com.outbound.impl.metric;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ActivityMetric {

	private String domain;
	private String activityName;
	private String templateName;
	private String startTime;
	private String endTime;
	private int status;
	private int callrate;
	private int maxCall;
	private int maxCurrent;
	private String currentBatch;
	private int currentRound;

	private int roterNum;
	private int batchNum;
	private int completeBatchNum;
	private int rosterNumDay;
	private int completeBatchNumDay;
	private int outCallNum;
	private int unCallNum;
	private int answerCallNum;
	private int dncNum;

	private Map<Integer, Integer> hours_callout;
	private Map<Integer, Integer> hours_callrate;
	private Map<Integer, Integer> hours_callAnswer;

	public ActivityMetric() {
		hours_callout = new HashMap<Integer, Integer>();
		hours_callrate = new HashMap<Integer, Integer>();
		hours_callAnswer = new HashMap<Integer, Integer>();
		for (int i = 0; i < 24; i++) {
			hours_callout.put(i, 0);
			hours_callrate.put(i, 0);
			hours_callAnswer.put(i, 0);
		}
	}

	public String toString() {
		return "roterNum-" + roterNum + "|" + "batchNum-" + batchNum + "|" + "outCallNum-" + outCallNum + "|"
				+ "unCallNum-" + unCallNum + "|" + "answerCallNum-" + answerCallNum + "|" + "dncNum-" + dncNum + "|";
	}

	public void clear() {
		roterNum = 0;
		batchNum = 0;
		outCallNum = 0;
		unCallNum = 0;
		answerCallNum = 0;
		dncNum = 0;
		rosterNumDay = 0;
		completeBatchNumDay = 0;
		currentBatch = "";
		currentRound = 0;
		hours_callout.clear();
		hours_callrate.clear();
		hours_callAnswer.clear();
	}

	public int getRoterNum() {
		return roterNum;
	}

	public void setRoterNum(int roterNum) {
		this.roterNum = roterNum;
	}

	public void addRoterNum(int num) {
		this.roterNum += num;
	}

	public int getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(int batchNum) {
		this.batchNum = batchNum;
	}

	public void addBatchNum(int num) {
		this.batchNum += num;
	}

	public int getOutCallNum() {
		return outCallNum;
	}

	public void setOutCallNum(int outCallNum) {
		this.outCallNum = outCallNum;
	}

	public void addOutCallNum() {
		this.outCallNum++;

		int current_hour = getCurrentHour();
		if (hours_callout.get(current_hour) != null) {
			int current_call = hours_callout.get(current_hour);
			current_call++;
			hours_callout.put(current_hour, current_call);
		} else {
			hours_callout.put(current_hour, 1);
		}
		unCallNum = roterNum - outCallNum;
		if (unCallNum <= 0) {
			unCallNum = 0;
		}
	}

	public void setCallRate(int rate) {
		this.callrate = rate;
		int current_hour = getCurrentHour();
		if (hours_callrate.get(current_hour) != null) {
			int current_rate = hours_callrate.get(current_hour);
			if (rate > current_rate) {
				hours_callrate.put(current_hour, rate);
			}
		} else {
			hours_callrate.put(current_hour, rate);
		}
		if(rate >= maxCall){
			maxCall = rate;
		}
	}

	public int getUnCallNum() {
		return unCallNum;
	}

	public void setUnCallNum(int unCallNum) {
		this.unCallNum = unCallNum;
	}

	public void addUnCallNum() {
		this.unCallNum++;
	}

	public int getAnswerCallNum() {
		return answerCallNum;
	}

	public void setAnswerCallNum(int answerCallNum) {
		this.answerCallNum = answerCallNum;
	}

	public void addAnswerCallNum() {
		int current_hour = getCurrentHour();
		if (hours_callAnswer.get(current_hour) != null) {
			int current_call = hours_callAnswer.get(current_hour);
			current_call++;
			hours_callAnswer.put(current_hour, current_call);
		} else {
			hours_callAnswer.put(current_hour, 1);
		}
		this.answerCallNum++;
	}

	public int getDncNum() {
		return dncNum;
	}

	public void setDncNum(int dncNum) {
		this.dncNum = dncNum;
	}

	public void addDncNum() {
		this.dncNum++;
	}

	private int getCurrentHour() {
		Calendar rightNow = Calendar.getInstance();
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		return hour;
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

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCallrate() {
		return callrate;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getCompleteBatchNum() {
		return completeBatchNum;
	}

	public void setCompleteBatchNum(int completeBatchNum) {
		this.completeBatchNum = completeBatchNum;
	}

	public int getMaxCall() {
		return maxCall;
	}

	public void setMaxCall(int maxCall) {
		this.maxCall = maxCall;
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

	public int getRosterNumDay() {
		return rosterNumDay;
	}

	public void setRosterNumDay(int rosterNumDay) {
		this.rosterNumDay = rosterNumDay;
	}
	
	public void addRosterNumDay(int rosterNumDay) {
		this.rosterNumDay += rosterNumDay;
	}

	public int getCompleteBatchNumDay() {
		return completeBatchNumDay;
	}

	public void setCompleteBatchNumDay(int completeBatchNumDay) {
		this.completeBatchNumDay = completeBatchNumDay;
	}
	
	public void addCompleteBatchNumDay() {
		this.completeBatchNumDay ++;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getMaxCurrent() {
		return maxCurrent;
	}

	public void setMaxCurrent(int maxCurrent) {
		this.maxCurrent = maxCurrent;
	}
}
