package com.outbound.job;

import java.util.List;

import org.apache.log4j.Logger;
import com.outbound.impl.TaskContainer;
import com.outbound.impl.activity.ActivityThread;
import com.outbound.impl.activity.RosterPool;
import com.outbound.impl.activity.RosterThread;
import com.outbound.impl.metric.ActivityMetric;
import com.outbound.impl.metric.BatchMetric;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.impl.plan.PlanActivityThread;
import com.outbound.impl.plan.PlanRosterPool;
import com.outbound.impl.plan.PlanRosterThread;
import com.outbound.impl.util.AOUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.OutboundPolicyInfo;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.OutboundPolicyInfoDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.TimeUtil;

public class ActivityUtil
{
	private static Logger logger = Logger.getLogger(ActivityUtil.class.getName());
	private static ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	private static OutboundPolicyInfoDAO outboundPolicyInfoDao = (OutboundPolicyInfoDAO) ApplicationContextUtil
			.getApplicationContext().getBean("OutboundPolicyInfoDAO");

	private static RosterBatchInfoDAO rosterBatchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");

	// public static TelephoneDictionary dict;

	public static void init()
	{
		// dict= new TelephoneDictionary();
		List<ActivityInfo> infos = activityDao.getAllActivityInfos();
		if (infos != null)
		{
			logger.info("init activity List number:" + infos.size());

			for (ActivityInfo info : infos)
			{
				if (info.getStatus() == 1 || info.getStatus() == 2)
				{
					// info.setActivityExecuteTime(TimeUtil.getCurrentTimeStr());
					activityDao.updateActivityInfo(info);

					OutboundPolicyInfo pinfo = outboundPolicyInfoDao.getTOutboundPolicyInfos(info.getDomain(),
							info.getPolicyName());
					if (info != null)
					{
						TaskContainer.policyMap.put(info.getName() + ":" + info.getDomain(), pinfo);
					}
					if (info.getActivityExecuteType() == 0)
					{
						
						RosterPool rosterPool = new RosterPool(AOUtil.genernateRosterPoolId(info.getDomain(), info.getName())); 
						
						ActivityThread activityThread = new ActivityThread(info, pinfo,rosterPool);
						Thread aThread = new Thread(activityThread,AOUtil.genernateActivityThdId(info.getDomain(), info.getName()) );
						aThread.start();
						
						RosterThread rosterThread = new RosterThread(info,rosterPool);
						Thread rThread = new Thread(rosterThread, AOUtil.genernateRosterThdId(info.getDomain(), info.getName()));
						rThread.start();

						if (info.getStatus() == 2)
						{
							activityThread.pauseTask();
						}
						/**
						 * @author zzj
						 * 重启时普通任务 增加定时任务自动关闭
						 */
						long now = System.currentTimeMillis();
						long finshTime = TimeUtil.getDateTime(info.getEndDatetime());
						int timeOut = Integer.parseInt((finshTime - now)/1000 + "");
						logger.info((finshTime - now) + "  ms=====>s  " + timeOut);
						if(timeOut > 0) {
							SimpleJobManager.addJob(AOUtil.genernateActivityThdId(info.getDomain(), info.getName()), StopTask.class,timeOut, 0, 0);
						}

					} else if (info.getActivityExecuteType() == 1)
					{
						logger.info("planactivity name" + AOUtil.genernateRosterThdId(info.getDomain(), info.getName()));
						PlanRosterPool planRosterPool = new PlanRosterPool(AOUtil.genernateRosterPoolId(info.getDomain(), info.getName()));
						
						PlanActivityThread planActivityThread = new PlanActivityThread(info, pinfo, planRosterPool);
						Thread paThread = new Thread(planActivityThread, AOUtil.genernateActivityThdId(info.getDomain(), info.getName()));
						paThread.start();
						
						PlanRosterThread planRosterThread = new PlanRosterThread(info, planRosterPool);
						Thread prThread = new Thread(planRosterThread, AOUtil.genernateRosterThdId(info.getDomain(), info.getName()));
						prThread.start();
						
						planRosterThread.getFirstUncallBatch();
						
						if (info.getStatus() == 2)
						{
							planActivityThread.pauseTask();
						}
						if (info.getActivityExecuteType() == 1)
						{
							String rosterName = AOUtil.genernateRosterThdId(info.getDomain(), info.getName());
							String excuteTime = info.getActivityExecuteTime();
							if (excuteTime.contains("/"))
							{
								excuteTime = excuteTime.substring(3, excuteTime.length());
								excuteTime = excuteTime.substring(0, excuteTime.indexOf(" "));
								int excuteTimes = Integer.parseInt(excuteTime) * 60 * 1000;
								int waitTime = Integer.parseInt(excuteTime) * 60;
								SimpleJobManager.addJob(rosterName, ActivityCronExcuteJob.class, waitTime, excuteTimes, -1);
							} else
							{
								CronJobManager.addJob(rosterName, ActivityCronExcuteJob.class, info.getActivityExecuteTime());
							}
						}
					}

					//当在结果回传的时间选择上 活动结束后/每20分钟/每天九点
					if (info.getActivityBackAddrType() == 2)
					{
						String activityName = info.getId() + "_back";
						String excuteTime = info.getActivityBackTime();
						if (excuteTime.contains("/"))
						{
							excuteTime = excuteTime.substring(3, excuteTime.length());
							excuteTime = excuteTime.substring(0, excuteTime.indexOf(" "));
							int excuteTimes = Integer.parseInt(excuteTime) * 60 * 1000;
							SimpleJobManager.addJob(activityName, RosterExportJob.class, 0, excuteTimes, -1);
						} else
						{
							CronJobManager.addJob(activityName, RosterExportJob.class, info.getActivityBackTime());
						}
					}

					String name = info.getName() + ":" + info.getDomain();
					ActivityMetric metric = MetricUtil.actMetrics.get(name);
					if (metric == null)
					{
						metric = new ActivityMetric();
						MetricUtil.actMetrics.put(name, metric);
					}

					metric.setActivityName(info.getName());
					metric.setOutCallNum(info.getOutCallNum());
					metric.setAnswerCallNum(info.getAnswerCallNum());
				}
			}
		}

		List<RosterBatchInfo> binfos = rosterBatchDao.getTodayRosterBatchInfos();
		for (RosterBatchInfo ibnfo : binfos)
		{
			String bname = ibnfo.getBatchId() + ":" + ibnfo.getCallRound() + ":" + ibnfo.getActivityName() + ":"
					+ ibnfo.getDomain();
			BatchMetric metric = MetricUtil.batchMetrics.get(bname);
			if (metric == null)
			{
				metric = new BatchMetric();
				MetricUtil.batchMetrics.put(bname, metric);
			}
			metric.setActivityName(ibnfo.getActivityName());
			metric.setDomain(ibnfo.getDomain());
			metric.setBatchName(ibnfo.getBatchId());
			metric.setRoterNum(ibnfo.getRoterNum());
			metric.setAnswerCallNum(ibnfo.getAnswerCallNum());
			metric.setFail1Num(ibnfo.getFail1Num());
			metric.setFail2Num(ibnfo.getFail2Num());
			metric.setFail3Num(ibnfo.getFail3Num());
			metric.setFail4Num(ibnfo.getFail4Num());
			metric.setRound(ibnfo.getCallRound());
			metric.setFail5Num(ibnfo.getFail5Num());
			metric.setFailOtherNum(ibnfo.getFailOtherNum());
			metric.setOutCallNum(ibnfo.getOutCallNum());
			metric.setStatus(ibnfo.getStatus());

		}

	}
}
