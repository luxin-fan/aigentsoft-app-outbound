package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CallRecord {

	private int id;
	private String uuid;

	private String caller;
	private String callee;

	//0 internal 1 callout 2 callin
	private int callType;

	private String sipcallId;
	private String sipNetIp;

	private String startTime;
	private String answerTime;
	private String holdTime;
	private String transferTime;
	private String hangupTime;

	private String hangupCause;

	private int queueTime;
	private int ringTime;
	private int lastTime;
	private int answerCallTime;
	
	private String intrunkInfo;
	private String gatewayInfo;

	private int isRecord;
	private int recordTime;
	private String recordPath;
	private String recordServer;

	private int billingTime;
	private float billingMoney;
	private int switchId;
	private Integer tenantId;

	private String agentId;
	private String skill;
	private String readCodec;
	private String writeCodec;
	private String localMediaIp;
	private String remoteMediaIp;
	private int localMediaPort;
	private int remoteMediaPort;
	
	public String toString() {
		return new StringBuilder().append("uuid-  ").append(uuid)
				.append("| callType- ").append(callType)
				.append("| caller- ").append(caller)
				.append("| callee- ").append(callee)
				.append("| domain- ").append(sipNetIp)
				.append("| agentId- ").append(agentId)
				.append("| skill- ").append(skill)
				.append("| queueTime- ").append(queueTime)
				.append("| ringTime- ").append(ringTime)
				.append("| lastTime- ").append(lastTime)
				.append("| startTime- ").append(startTime)
				.append("| answerTime- ").append(answerTime)
				.append("| readCodec- ").append(readCodec)
				.append("| writeCodec- ").append(writeCodec)
				.append("| localMediaIp- ").append(localMediaIp).append(":").append(localMediaPort)
				.append("| remoteMediaIp- ").append(remoteMediaIp).append(":").append(remoteMediaPort)
				.append("| hangupCause- " + hangupCause).toString();
	}
	
	
	public int getAnswerCallTime() {
		return answerCallTime;
	}


	public void setAnswerCallTime(int answerCallTime) {
		this.answerCallTime = answerCallTime;
	}


	public int getSwitchId() {
		return switchId;
	}

	public void setSwitchId(int switchId) {
		this.switchId = switchId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getCallee() {
		return callee;
	}

	public void setCallee(String callee) {
		this.callee = callee;
	}

	public int getCallType() {
		return callType;
	}

	public void setCallType(int callType) {
		this.callType = callType;
	}

	public String getSipcallId() {
		return sipcallId;
	}

	public void setSipcallId(String sipcallId) {
		this.sipcallId = sipcallId;
	}

	public String getHangupCause() {
		return hangupCause;
	}

	public void setHangupCause(String hangupCause) {
		this.hangupCause = hangupCause;
	}

	public int getRingTime() {
		return ringTime;
	}

	public void setRingTime(int ringTime) {
		this.ringTime = ringTime;
	}

	public int getLastTime() {
		return lastTime;
	}

	public void setLastTime(int lastTime) {
		this.lastTime = lastTime;
	}

	public String getGatewayInfo() {
		return gatewayInfo;
	}

	public void setGatewayInfo(String gatewayInfo) {
		this.gatewayInfo = gatewayInfo;
	}

	public int getIsRecord() {
		return isRecord;
	}

	public void setIsRecord(int isRecord) {
		this.isRecord = isRecord;
	}

	public int getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(int recordTime) {
		this.recordTime = recordTime;
	}

	public String getRecordPath() {
		return recordPath;
	}

	public void setRecordPath(String recordPath) {
		this.recordPath = recordPath;
	}

	public String getRecordServer() {
		return recordServer;
	}

	public void setRecordServer(String recordServer) {
		this.recordServer = recordServer;
	}

	public String getIntrunkInfo() {
		return intrunkInfo;
	}

	public void setIntrunkInfo(String intrunkInfo) {
		this.intrunkInfo = intrunkInfo;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(String answerTime) {
		this.answerTime = answerTime;
	}

	public String getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(String holdTime) {
		this.holdTime = holdTime;
	}

	public String getTransferTime() {
		return transferTime;
	}

	public void setTransferTime(String transferTime) {
		this.transferTime = transferTime;
	}

	public String getHangupTime() {
		return hangupTime;
	}

	public void setHangupTime(String hangupTime) {
		this.hangupTime = hangupTime;
	}

	public String getSipNetIp() {
		return sipNetIp;
	}

	public void setSipNetIp(String sipNetIp) {
		this.sipNetIp = sipNetIp;
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public int getBillingTime() {
		return billingTime;
	}

	public void setBillingTime(int billingTime) {
		this.billingTime = billingTime;
	}

	public float getBillingMoney() {
		return billingMoney;
	}

	public void setBillingMoney(float billingMoney) {
		this.billingMoney = billingMoney;
	}

	public int getQueueTime() {
		return queueTime;
	}

	public void setQueueTime(int queueTime) {
		this.queueTime = queueTime;
	}

	public String getReadCodec() {
		return readCodec;
	}

	public void setReadCodec(String readCodec) {
		this.readCodec = readCodec;
	}

	public String getWriteCodec() {
		return writeCodec;
	}

	public void setWriteCodec(String writeCodec) {
		this.writeCodec = writeCodec;
	}

	public String getLocalMediaIp() {
		return localMediaIp;
	}

	public void setLocalMediaIp(String localMediaIp) {
		this.localMediaIp = localMediaIp;
	}

	public String getRemoteMediaIp() {
		return remoteMediaIp;
	}

	public void setRemoteMediaIp(String remoteMediaIp) {
		this.remoteMediaIp = remoteMediaIp;
	}

	public int getLocalMediaPort() {
		return localMediaPort;
	}

	public void setLocalMediaPort(int localMediaPort) {
		this.localMediaPort = localMediaPort;
	}

	public int getRemoteMediaPort() {
		return remoteMediaPort;
	}

	public void setRemoteMediaPort(int remoteMediaPort) {
		this.remoteMediaPort = remoteMediaPort;
	}

}
