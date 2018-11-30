package com.outbound.impl.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CallResult {

	private String id;
	private String activity_id;
	private String rosterinfo_id;
	private String number;
	private String callId;
	private String callTime;
	private String result;
	private int resultCode;

	private String allocate_stime;
	private String allocate_etime;


	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public String getCallTime() {
		return callTime;
	}

	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getAllocate_stime() {
		return allocate_stime;
	}

	public void setAllocate_stime(String allocate_stime) {
		this.allocate_stime = allocate_stime;
	}

	public String getAllocate_etime() {
		return allocate_etime;
	}

	public void setAllocate_etime(String allocate_etime) {
		this.allocate_etime = allocate_etime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRosterinfo_id() {
		return rosterinfo_id;
	}

	public void setRosterinfo_id(String rosterinfo_id) {
		this.rosterinfo_id = rosterinfo_id;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
