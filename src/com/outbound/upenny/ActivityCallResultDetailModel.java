package com.outbound.upenny;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 获取活动拨打结果信息
 * @author duanlsh
 *
 */
@XmlRootElement
public class ActivityCallResultDetailModel {

	
	private String duration;
	
	private String start;
	
	private String state;
	
	private String callCount;
	

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCallCount() {
		return callCount;
	}

	public void setCallCount(String callCount) {
		this.callCount = callCount;
	}

	
}
