package com.outbound.dialer.util;

import org.apache.log4j.Logger;
import com.ces.redis.RedisDevice;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.outbound.object.Roster;
import com.outbound.object.RosterInfo;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;

public class RedisUtil
{

	private static Logger logger = Logger.getLogger(RedisUtil.class.getName());

	public static RedisDevice redisDevice = null;

	static java.lang.reflect.Type type = new TypeToken<RosterInfo>()
	{
	}.getType();

	static Gson gson = new Gson();

	public static void init()
	{
		redisDevice = new RedisDevice();
	}

	// key format activityName_batchName_roster_id
	public static void addRoster(RosterInfo info)
	{
		String rosterId = info.getActivityName() + "_" + info.getBatchName() + "_roster_" + info.getRosterId();
		boolean result = redisDevice.setCallRoster(rosterId, gson.toJson(info));
		logger.info("### key-[" + rosterId + "] add result " + result);
		
		/*if (result)
		{
			expireRoster(info);
		}*/

		info.setStatus(1);

	}

	public static void updateRoster(RosterInfo info)
	{
		String rosterId = info.getActivityName() + "_" + info.getBatchName() + "_roster_" + info.getRosterId();
		boolean result = redisDevice.setCallRoster(rosterId, gson.toJson(info));
		logger.info("### key-[" + rosterId + "] update result " + result);
	}

	public static void expireRoster(RosterInfo info)
	{
		String rosterId = info.getActivityName() + "_" + info.getBatchName() + "_roster_" + info.getRosterId();
		boolean result = redisDevice.expireCallRoster(rosterId);
		logger.info("### key-[" + rosterId + "] expire 120s result " + result);
	}

	public static void delRoster(RosterInfo info)
	{
		String rosterId = info.getActivityName() + "_" + info.getBatchName() + "_roster_" + info.getRosterId();
		Long result = redisDevice.delCallRoster(rosterId);
		logger.info("### key-[" + rosterId + "] delete result " + result);
	}
	
	/**
	 * @author fanlx
	 * @date 2018.10.16
	 * */
	public static void delRoster(Roster roster) {
		String rosterId = roster.getActivityName() + "_" + roster.getBatchName() + "_roster_" + roster.getId();
		Long result = redisDevice.delCallRoster(rosterId);
		logger.info("### key-[" + rosterId + "] delete result " + result);
	}

	public static RosterInfo getRoster(String activityName, String batchName, String roster_id)
	{
		String rosterId = activityName + "_" + batchName + "_roster_" + roster_id;
		String result = redisDevice.getCallRoster(rosterId);
		logger.info("### key-[" + rosterId + "] get result " + result);
		if (result == null)
		{
			return null;
		}
		RosterInfo info = gson.fromJson(result, type);
		return info;
	}

	public static int getKeys(String activityName)
	{
		String rosterId = activityName + "_*";
		int num = redisDevice.getkeysNum(rosterId);
		// logger.info("### key-" + rosterId +" get keys num " + num);
		return num;
	}

	public static int getBatchKeys(String batchName)
	{
		String rosterId = "*_" + batchName + "_*";
		int num = redisDevice.getkeysNum(rosterId);
		// logger.info("### key-" + rosterId +" get keys num " + num);
		return num;
	}
}
