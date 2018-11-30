package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RosterBatchInfo {

	private int id;
	
	//0 未开始， 1 进行中， 2 已完成
	private int status;
	private String batchId;
	private int activityId;
	private String activityName;
	private String templateName;
	private String domain;
	private String createTime;
	private String startTime;
	private String completeTime;
	
	private int roterNum;
	private int outCallNum;
	private int unCallNum;
	private int answerCallNum;
	private int dncNum;
	private int effectiveNum;
	
	private int callRound;
	
	//空号
	private int fail1Num;
	//关机
	private int fail2Num;
	//用户正忙
	private int fail3Num;
	//振铃没接听
	private int fail4Num;
	//停机
	private int fail5Num;
	//无法接通
	private int fail6Num;
	//正在通话中
	private int fail7Num;
	//其它
	private int failOtherNum;
	
	public String toString(){
		return batchId +"|" + templateName +"|" + activityName +"|" + domain;
	}
	
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}

	public int getRoterNum() {
		return roterNum;
	}

	public void setRoterNum(int roterNum) {
		this.roterNum = roterNum;
	}

	public int getOutCallNum() {
		return outCallNum;
	}

	public void setOutCallNum(int outCallNum) {
		this.outCallNum = outCallNum;
	}

	public int getUnCallNum() {
		return unCallNum;
	}

	public void setUnCallNum(int unCallNum) {
		this.unCallNum = unCallNum;
	}

	public int getAnswerCallNum() {
		return answerCallNum;
	}

	public void setAnswerCallNum(int answerCallNum) {
		this.answerCallNum = answerCallNum;
	}

	public int getDncNum() {
		return dncNum;
	}

	public void setDncNum(int dncNum) {
		this.dncNum = dncNum;
	}

	public int getFail1Num() {
		return fail1Num;
	}

	public void setFail1Num(int fail1Num) {
		this.fail1Num = fail1Num;
	}

	public int getFail2Num() {
		return fail2Num;
	}

	public void setFail2Num(int fail2Num) {
		this.fail2Num = fail2Num;
	}

	public int getFail3Num() {
		return fail3Num;
	}

	public void setFail3Num(int fail3Num) {
		this.fail3Num = fail3Num;
	}

	public int getFail4Num() {
		return fail4Num;
	}

	public void setFail4Num(int fail4Num) {
		this.fail4Num = fail4Num;
	}

	public int getFailOtherNum() {
		return failOtherNum;
	}

	public void setFailOtherNum(int failOtherNum) {
		this.failOtherNum = failOtherNum;
		int fail = outCallNum - answerCallNum - fail1Num - fail2Num
				- fail3Num - fail4Num - fail5Num - fail6Num - fail7Num - failOtherNum;
		if(fail > 0){
			this.setFail4Num(fail);
		}
	}

	public int getFail5Num() {
		return fail5Num;
	}

	public void setFail5Num(int fail5Num) {
		this.fail5Num = fail5Num;
	}
	
	public void addFail5Num() {
		this.fail5Num ++;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getCallRound() {
		return callRound;
	}

	public void setCallRound(int callRound) {
		this.callRound = callRound;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getFail6Num() {
		return fail6Num;
	}


	public void setFail6Num(int fail6Num) {
		this.fail6Num = fail6Num;
	}


	public int getFail7Num() {
		return fail7Num;
	}


	public void setFail7Num(int fail7Num) {
		this.fail7Num = fail7Num;
	}


	public int getEffectiveNum() {
		return effectiveNum;
	}

	public void setEffectiveNum(int effectiveNum) {
		this.effectiveNum = effectiveNum;
	}
	
	public void addEffectiveNum() {
		this.effectiveNum ++;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityName == null) ? 0 : activityName.hashCode());
		result = prime * result + ((batchId == null) ? 0 : batchId.hashCode());
		result = prime * result + id;
		result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RosterBatchInfo other = (RosterBatchInfo) obj;
		if (activityName == null) {
			if (other.activityName != null)
				return false;
		} else if (!activityName.equals(other.activityName))
			return false;
		if (batchId == null) {
			if (other.batchId != null)
				return false;
		} else if (!batchId.equals(other.batchId))
			return false;
		if (id != other.id)
			return false;
		if (templateName == null) {
			if (other.templateName != null)
				return false;
		} else if (!templateName.equals(other.templateName))
			return false;
		return true;
	}
}
