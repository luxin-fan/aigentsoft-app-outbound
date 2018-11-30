package com.outbound.impl;

import java.util.HashMap;
import java.util.Map;

import com.outbound.impl.acd.Agent;
import com.outbound.impl.activity.ActivityThread;
import com.outbound.impl.activity.RosterThread;
import com.outbound.impl.auto.AutoCallThread;
import com.outbound.impl.auto.PlanAutoCallThread;
import com.outbound.impl.plan.PlanActivityThread;
import com.outbound.impl.plan.PlanRosterThread;
import com.outbound.object.OutboundPolicyInfo;

public class TaskContainer {
	
	public static Map<String, AutoCallThread> autoCallActivityMap = new HashMap<String, AutoCallThread>();
	//public static Map<String, PredictiveThread> predictiveTaskMap = new HashMap<String, PredictiveThread>();
	private static Map<String, ActivityThread> activityMap = new HashMap<String, ActivityThread>();
	private static Object activityLock = new Object();
	
	private static Map<String, RosterThread> rosterMap = new HashMap<String, RosterThread>();
	private static Object rosterLock = new Object();
	
	private static Object planRosterLock = new Object();
	
	private static Object planActivityLock = new Object();
	
	public static Map<String, PlanAutoCallThread> planAutoCallActivityMap = new HashMap<String, PlanAutoCallThread>();
	
	public static Map<String, PlanActivityThread> planActivityMap = new HashMap<String, PlanActivityThread>(); 
	
	public static Map<String, PlanRosterThread> planRosterMap = new HashMap<String, PlanRosterThread>();
	
    public static Map<String, String> callRosterMap = new HashMap<String, String>();
    
    public static Map<String, String> callTaskMap = new HashMap<String, String>();
    
    public static Map<String, Agent> callAgentMap = new HashMap<String, Agent>();
    
    public static Map<String, OutboundPolicyInfo> policyMap = new HashMap<String, OutboundPolicyInfo>();
    
    // 1 alerting 2 established 3 hangup
    public static Map<String, Integer> callState = new HashMap<String, Integer>();
	
	public static SessionImpl sImp = new SessionImpl();
	
	
	public static void addActivityThread(String activityName, ActivityThread activityThread)
	{
		synchronized (activityLock)
		{
			activityMap.put(activityName, activityThread);
		}
	}
	
	public static ActivityThread findActivityThread(String activityName)
	{
		synchronized (activityLock)
		{
			return activityMap.get(activityName);
		}
	}
	
	public static void deleteActivityThread(String activityName)
	{
		synchronized (activityLock)
		{
			if (activityMap.containsKey(activityName))
			{
				activityMap.remove(activityName);
			}
		}
	}
	
	public static void addRosterThread(String activityName, RosterThread rosterThread)
	{
		synchronized (rosterLock)
		{
			rosterMap.put(activityName,  rosterThread);
		}
	}
	
	public static RosterThread findRosterThread(String activityName)
	{
		synchronized (rosterLock)
		{
			return rosterMap.get(activityName);
		}
	}
	
	public static void deleteRosterThread(String activityName)
	{
		synchronized (rosterLock)
		{
			if (rosterMap.containsKey(activityName))
			{
				rosterMap.remove(activityName);
			}
		}
	}
	
	//计划任务
	public static void addPlanActivityThread(String activityName, PlanActivityThread activityThread)
	{
		synchronized (planActivityLock)
		{
			planActivityMap.put(activityName, activityThread);
		}
	}
	
	public static PlanActivityThread findPlanActivityThread(String activityName)
	{
		synchronized (planActivityLock)
		{
			return planActivityMap.get(activityName);
		}
	}
	
	public static void deletePlanActivityThread(String activityName)
	{
		synchronized (planActivityLock)
		{
			if (planActivityMap.containsKey(activityName))
			{
				planActivityMap.remove(activityName);
			}
		}
	}
	
	public static void addPlanRosterThread(String activityName, PlanRosterThread rosterThread)
	{
		synchronized (planRosterLock)
		{
			planRosterMap.put(activityName,  rosterThread);
		}
	}
	
	public static PlanRosterThread findPlanRosterThread(String activityName)
	{
		synchronized (planRosterLock)
		{
			return planRosterMap.get(activityName);
		}
	}
	
	public static void deletePlanRosterThread(String activityName)
	{
		synchronized (planRosterLock)
		{
			if (planRosterMap.containsKey(activityName))
			{
				planRosterMap.remove(activityName);
			}
		}
	}
}
