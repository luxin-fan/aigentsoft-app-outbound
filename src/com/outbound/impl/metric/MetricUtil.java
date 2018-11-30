package com.outbound.impl.metric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.outbound.dialer.util.RedisUtil;
import com.outbound.impl.util.RosterResultW;
import com.outbound.object.DbColumn;
import com.outbound.object.RosterBatch;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.RosterInfo;
import com.outbound.upenny.UpennyRosterRequest;

public class MetricUtil
{
	public static Logger logger = Logger.getLogger(MetricUtil.class.getName());

	public static Gson gson = new Gson();

	public static java.lang.reflect.Type typeDBCol = new TypeToken<List<DbColumn>>()
	{
	}.getType();
	public static java.lang.reflect.Type typeList = new TypeToken<List<String>>()
	{
	}.getType();
	public static java.lang.reflect.Type mapList = new TypeToken<HashMap<String, String>>()
	{
	}.getType();

	public static Map<String, ActivityMetric> actMetrics = new HashMap<String, ActivityMetric>();
	public static Map<String, BatchMetric> batchMetrics = new HashMap<String, BatchMetric>();
	public static List<RosterBatchInfo> completeBatchs = new CopyOnWriteArrayList<RosterBatchInfo>();

	public static Map<String, UpennyRosterRequest> uppenyJobMap = new HashMap<String, UpennyRosterRequest>();

	public static void clear()
	{
		uppenyJobMap.clear();
		completeBatchs.clear();
		actMetrics.clear();
		batchMetrics.clear();
		//dncRosters.clear();
	}

	public synchronized static void addRostersDay(String activityName, String domain, int rosterNum) {
		String name = activityName + ":" + domain;
		ActivityMetric metric = actMetrics.get(name);
		if (metric == null) {
			metric = new ActivityMetric();
			metric.setDomain(domain);
			metric.setActivityName(activityName);
			actMetrics.put(name, metric);
			logger.info(name + " |" + activityName + "| " + " is null create activity metric !");
		}
		metric.addRosterNumDay(rosterNum);
		metric.addCompleteBatchNumDay();
	}

	// activity metric key = activityName:domain
	// batch metric key = batchName:round:activityName:domain
	public synchronized static void addCallOut(RosterInfo info)
	{
		String name = info.getActivityName() + ":" + info.getDomain();
		ActivityMetric metric = actMetrics.get(name);
		if (metric == null)
		{
			metric = new ActivityMetric();
			metric.setDomain(info.getDomain());
			metric.setActivityName(info.getActivityName());
			actMetrics.put(name, metric);
			logger.info(name + " |" + info.getId() + "| " + " is null create activity metric !");
		}

		metric.addOutCallNum();
		metric.setCallRate(RedisUtil.getKeys(metric.getActivityName()));
		metric.setCurrentBatch(info.getBatchName());
		metric.setCurrentRound(info.getCallRound());

		String bname = info.getBatchName() + ":" + info.getCallRound() + ":" + info.getActivityName() + ":"
				+ info.getDomain();
		bname = bname.trim();
		BatchMetric bmetric = batchMetrics.get(bname);
		if (bmetric == null)
		{
			bmetric = new BatchMetric();
			bmetric.setDomain(info.getDomain());
			bmetric.setActivityName(info.getActivityName());
			bmetric.setRound(info.getCallRound());
			bmetric.setBatchName(info.getBatchName());
			batchMetrics.put(bname, bmetric);
			logger.info(bname + " |" + info.getId() + "| " + " is null create batch metric !");
		}
		bmetric.addOutCallNum();
		logger.info(" ### " + info.getBatchName() + "| " + info.getId() + "|" + " add outcallnum ["
				+ bmetric.getOutCallNum() + "]");
	}

	public synchronized static void addBatchInfo(RosterBatchInfo info)
	{
		String bname = info.getBatchId() +":"+info.getActivityName()+ ":" + info.getDomain();
		logger.info(" ## " + bname + "  roster add batch info!");
		completeBatchs.add(info);
	}
	
	public synchronized static void removeBatchInfo(RosterBatchInfo batchInfo)
	{
		String bname = batchInfo.getBatchId() +":"+batchInfo.getActivityName()+ ":" + batchInfo.getDomain();
		logger.info(" ## " + bname + "  roster remove batch info!");
		MetricUtil.completeBatchs.remove(batchInfo);
	}

	/**
	 * 强行删除批次时进行共享资源的清理
	 * 
	 * @author fanlx
	 * @date 2018.10.16
	 */
	public synchronized static void removeRosterBatch(RosterBatch rosterBatch) {
		String bname = rosterBatch.getBatchId() + ":" + rosterBatch.getCallRound() + ":" + rosterBatch.getActivityName()
				+ ":" + rosterBatch.getDomain();
		bname = bname.trim();
		BatchMetric bmetric = MetricUtil.batchMetrics.get(bname);
		if (bmetric != null) {
			MetricUtil.batchMetrics.remove(bname);
		}
	}

	public synchronized static void addCallResult(RosterResultW info) {
		String name = info.getActivity_id() + ":" + info.getDomain();
		ActivityMetric metric = actMetrics.get(name);
		if (metric == null) {
			metric = new ActivityMetric();
			metric.setDomain(info.getDomain());
			metric.setActivityName(info.getActivity_id());
			actMetrics.put(name, metric);
			logger.info(name + " |" + info.getRosterinfo_id() + "| " + " is null create activity metric !");
		}
		
		if (info.getResultCode() == 0)
		{
			metric.addAnswerCallNum();
		}

		if (info.getRound() == 0)
		{
			info.setRound(1);
		}
		String bname = info.getBatch_id() + ":" + info.getRound() + ":" + info.getActivity_id() + ":"
				+ info.getDomain();
		bname = bname.trim();
		BatchMetric bmetric = batchMetrics.get(bname);
		if (bmetric == null)
		{
			bmetric = new BatchMetric();
			bmetric.setDomain(info.getDomain());
			bmetric.setActivityName(info.getActivity_id());
			bmetric.setRound(info.getRound());
			bmetric.setBatchName(info.getBatch_id());
			batchMetrics.put(bname, bmetric);
			logger.info(bname + " |" + info.getRosterinfo_id() + "| " + " is null create batch metric !");
		}
		if (info.getResultCode() == 0)
		{
			bmetric.addAnswerCallNum();
		} else if (info.getResultCode() == 8)
		{
			bmetric.addFail1Num();
		} else if (info.getResultCode() == 6)
		{
			bmetric.addFail2Num();
		} else if (info.getResultCode() == 1)
		{
			bmetric.addFail3Num();
		} else if (info.getResultCode() == 9)
		{
			bmetric.addFail7Num();
		} else if (info.getResultCode() == 2 || info.getResultCode() == 3)
		{
			bmetric.addFail6Num();
		} else if (info.getResultCode() == 7)
		{
			bmetric.addFail5Num();
		} else if (info.getResultCode() == 11)
		{
			bmetric.addFail4Num();
		} else
		{
			bmetric.addFailOtherNum();
		}
	}

}
