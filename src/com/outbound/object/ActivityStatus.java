package com.outbound.object;

public class ActivityStatus {
	
	private String startTime;
	private String endTime;
	private int allCount;//应用流程执行次数
	private int validCount;//有效流程执行次数
	private int maxCallTime;//最大执行时长
	private Long avgCallTime;//平均执行时长
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
	public int getAllCount() {
		return allCount;
	}
	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}
	public int getValidCount() {
		return validCount;
	}
	public void setValidCount(int validCount) {
		this.validCount = validCount;
	}
	public int getMaxCallTime() {
		return maxCallTime;
	}
	public void setMaxCallTime(int maxCallTime) {
		this.maxCallTime = maxCallTime;
	}
	public Long getAvgCallTime() {
		return avgCallTime;
	}
	public void setAvgCallTime(Long avgCallTime) {
		this.avgCallTime = avgCallTime;
	}
	
	
}
