package com.outbound.impl.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RosterResultW {

	private String callId;

	// 0 success 1 noanswer 2 userbusy 3 fax 4 ivr 5 timeout 6 normal 7
	// wrongnumber
	private String callTime;
	
	private String hangupTime;
	
	//outboundEnd ,callend
	private String resultType;
	private String domain;

	private String result;
	private int resultCode;
	private String rosterinfo_id;
	private String batch_id;
	private String activity_id;
	private int round = 1;
	
	public String toString(){
		return "callId-" + callId +"|"+
				"callTime-" + callTime +"|"+
				"hangupTime-" + hangupTime +"|"+
				"resultType-" + resultType +"|"+
				"result-" + result +"|"+
				"resultCode-" + resultCode +"|"+
				"batch_id-" + batch_id +"|"+
				"round-" + round +"|"+
				"domain-" + domain +"|"+
				"rosterinfo_id-" + rosterinfo_id +"|"+
				"activity_id-" + activity_id +"|";
	}

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

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getRosterinfo_id() {
		return rosterinfo_id;
	}

	public void setRosterinfo_id(String rosterinfo_id) {
		this.rosterinfo_id = rosterinfo_id;
	}

	public String getHangupTime() {
		return hangupTime;
	}

	public void setHangupTime(String hangupTime) {
		this.hangupTime = hangupTime;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

}
