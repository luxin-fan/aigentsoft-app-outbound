package com.outbound.object;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchRosters
{
	private String batchId;
	private int maxCallRound;
	private int rosterNumber;
	private int callRosterNum;
	private int currentRound;
	private int batchLevel;
	private String domain;
	private String activityName;
	private String policyName;
	private String templateName;
	private int dncNum;
	
	private Map<Integer, Roster> rosterList = new HashMap<>();
	
	private Map<Integer,Roster> dncRosterList = new HashMap<Integer,Roster>();

	private List<OutboundRecallPolicy>policyList;
	
	public int getDncNum()
	{
		if(!dncRosterList.isEmpty()) {
			return dncRosterList.size();
		}
		return 0;
	}

	public void setDncNum(int dncNum)
	{
		this.dncNum = dncNum;
	}
	
	public String getTemplateName()
	{
		return templateName;
	}

	public void setTemplateName(String templateName)
	{
		this.templateName = templateName;
	}
	
	public String getActivityName()
	{
		return activityName;
	}

	public void setActivityName(String activityName)
	{
		this.activityName = activityName;
	}

	public String getPolicyName()
	{
		return policyName;
	}

	public void setPolicyName(String policyName)
	{
		this.policyName = policyName;
	}
	
	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}
	
	public List<OutboundRecallPolicy> getPolicyList()
	{
		return policyList;
	}

	public void setPolicyList(List<OutboundRecallPolicy> policyList)
	{
		this.policyList = policyList;
	}

	public String getBatchId()
	{
		return batchId;
	}

	public OutboundRecallPolicy findReCallPolicy(int callRound)
	{
		if (policyList != null)
		{
			for(OutboundRecallPolicy p:policyList  )
			{
				if (p.getRound() == callRound)
				{
					return p;
				}
			}
		}
		
		return null;
		
	}

	public int getMaxCallRound()
	{
		if (policyList != null)
		{
			return policyList.size();
		}
		
		return 0;
	}
	
	public Map<Integer, Roster> getDncRosterList() {
		return dncRosterList;
	}

	public void setDncRosterList(Map<Integer, Roster> dncRosterList) {
		this.dncRosterList = dncRosterList;
	}

	public int getRosterNumber()
	{
		return rosterNumber;
	}

	public void setRosterNumber(int rosterNumber)
	{
		this.rosterNumber = rosterNumber;
	}

	public int getCallRosterNum()
	{
		return callRosterNum;
	}

	public void setCallRosterNum(int callRosterNum)
	{
		this.callRosterNum = callRosterNum;
	}

	public int getCurrentRound()
	{
		return currentRound;
	}

	public void setCurrentRound(int currentRound)
	{
		this.currentRound = currentRound;
	}

	public int getBatchLevel()
	{
		return batchLevel;
	}

	public void setBatchLevel(int batchLevel)
	{
		this.batchLevel = batchLevel;
	}

	public Map<Integer, Roster> getRosterList()
	{
		return rosterList;
	}

	public void setRosterList(Map<Integer, Roster> rosterList)
	{
		this.rosterList = rosterList;
	}
	
	public BatchRosters(String batchId)
	{
		this.batchId = batchId;
	}
	
	public void addRoster(Roster roster)
	{
		if (!rosterList.containsKey(roster.getId()))
		{
			rosterList.put(roster.getId(), roster);
		}
	}
	
	public Roster findRoster(int rosterId)
	{
		return rosterList.get(rosterId);
	}
	
	public void delRoster(int rosterId)
	{
		if (rosterList.containsKey(rosterId))
		{
			rosterList.remove(rosterId);
		}
	}

	public int getCurrentRosterNum()
	{
		return rosterList.size();
	}
}
