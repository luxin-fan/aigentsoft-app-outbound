package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RosterBatch
{
	private int id;
	private int status; //0为执行 1正在执行 2完成 
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
	private int callRound;
	
	public int getCallRound()
	{
		return callRound;
	}

	public void setCallRound(int callRound)
	{
		this.callRound = callRound;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	public String getBatchId()
	{
		return batchId;
	}

	public void setBatchId(String batchId)
	{
		this.batchId = batchId;
	}

	public int getActivityId()
	{
		return activityId;
	}

	public void setActivityId(int activityId)
	{
		this.activityId = activityId;
	}

	public String getActivityName()
	{
		return activityName;
	}

	public void setActivityName(String activityName)
	{
		this.activityName = activityName;
	}

	public String getTemplateName()
	{
		return templateName;
	}

	public void setTemplateName(String templateName)
	{
		this.templateName = templateName;
	}

	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	public String getCreateTime()
	{
		return createTime;
	}

	public void setCreateTime(String createTime)
	{
		this.createTime = createTime;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	public String getCompleteTime()
	{
		return completeTime;
	}

	public void setCompleteTime(String completeTime)
	{
		this.completeTime = completeTime;
	}

	public int getRoterNum()
	{
		return roterNum;
	}

	public void setRoterNum(int roterNum)
	{
		this.roterNum = roterNum;
	}

	public int getOutCallNum()
	{
		return outCallNum;
	}

	public void setOutCallNum(int outCallNum)
	{
		this.outCallNum = outCallNum;
	}

	public int getUnCallNum()
	{
		return unCallNum;
	}

	public void setUnCallNum(int unCallNum)
	{
		this.unCallNum = unCallNum;
	}

	public int getAnswerCallNum()
	{
		return answerCallNum;
	}

	public void setAnswerCallNum(int answerCallNum)
	{
		this.answerCallNum = answerCallNum;
	}

	public int getDncNum()
	{
		return dncNum;
	}

	public void setDncNum(int dncNum)
	{
		this.dncNum = dncNum;
	}

	public String toString()
	{
		return batchId + "|" + templateName + "|" + activityName + "|" + domain;
	}
	
}
