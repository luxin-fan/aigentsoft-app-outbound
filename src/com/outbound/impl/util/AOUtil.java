package com.outbound.impl.util;

public class AOUtil
{
	public static String genernateActivityThdId(String domain, String activityName)
	{
		return "activity:" +activityName + "-" + domain;
	}
	
	public static String genernateRosterThdId(String domain, String activityName)
	{
		return "roster:" +activityName + "-" + domain;
	}
	
	public static String genernateRosterPoolId(String domain, String activityName)
	{
		return "rosterpoll:" +activityName + "-" + domain;
	}
}
