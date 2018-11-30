package com.outbound.job;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.outbound.impl.metric.MetricUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.ActivityInfoHistory;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.RosterInfo;
import com.outbound.object.RosterInfoHistory;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.ActivityInfoHistoryDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.dao.RosterHistoryDAO;
import com.outbound.object.util.ApplicationContextUtil;

public class DayClearJob implements Job
{

	private Logger logger = Logger.getLogger(DayClearJob.class.getName());

	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	private ActivityInfoHistoryDAO activityHDao = (ActivityInfoHistoryDAO) ApplicationContextUtil
			.getApplicationContext().getBean("ActivityInfoHistoryDAO");

	private RosterBatchInfoDAO batchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");

	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterDAO");

	private RosterHistoryDAO rosterHDao = (RosterHistoryDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterHistoryDAO");

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{

		long currentTiem = System.currentTimeMillis();
		logger.info("#@## day clear task start at " + new Timestamp(currentTiem).toString());
		MetricUtil.clear();
		processActivitys();

	}

	private void processActivitys()
	{
		List<ActivityInfo> acts = activityDao.getStoppedActivityInfos();
		if (acts != null)
		{
			for (ActivityInfo info : acts)
			{
				ActivityInfoHistory aHistory = new ActivityInfoHistory();
				try
				{
					BeanUtils.copyProperties(aHistory, info);
					int tBatchNum = batchDao.getTotalRosterBatchInfoNum(info.getRosterTemplateName(), info.getDomain());
					int cBatchNum = batchDao.getFinishedRosterBatchInfoNum(info.getRosterTemplateName(),
							info.getDomain());
					int tRosterNum = rosterDao.getAllRosterNums(info.getDomain(), info.getRosterTemplateName());
					// int todayRosterNum = rosterDao.gett(info.getDomain(),
					// info.getRosterTemplateName());
					// int cRosterNum =
					// rosterDao.getTodayCalledRosterNums(info.getDomain(),
					// info.getRosterTemplateName());
					int oRosterNum = rosterDao.getOutCallNums(info.getDomain(), info.getRosterTemplateName());
					int aRosterNum = rosterDao.getOutCallAnswerNums(info.getDomain(), info.getRosterTemplateName());

					aHistory.setBatchNum(tBatchNum);
					aHistory.setCompleteBatchNum(cBatchNum);
					aHistory.setRosterNum(tRosterNum);
					aHistory.setOutCallNum(oRosterNum);
					aHistory.setAnswerCallNum(aRosterNum);
				} catch (IllegalAccessException e)
				{
					logger.error(e);
				} catch (InvocationTargetException e)
				{
					logger.error(e);
				}
				activityHDao.createActivityInfoHistory(aHistory);
				activityDao.deleteActivityInfo(info);
			}
		}
	}

	private void processBatchs()
	{
		List<RosterBatchInfo> batchs = batchDao.getFinishRosterBatchInfos();
		if (batchs != null)
		{
			for (RosterBatchInfo info : batchs)
			{
				List<RosterInfo> rosters = rosterDao.findFinishBatchRosters(info.getDomain(), info.getBatchId());
				if (rosters != null)
				{
					for (RosterInfo rInfo : rosters)
					{
						RosterInfoHistory rInfoH = new RosterInfoHistory();
						try
						{
							BeanUtils.copyProperties(rInfoH, rInfo);
						} catch (IllegalAccessException e)
						{
							logger.error(e);
						} catch (InvocationTargetException e)
						{
							logger.error(e);
						}
						rosterHDao.createRosterInfoHistory(rInfoH);
						rosterDao.deleteRosterInfo(rInfo);
					}
				}
			}
		}
	}
}
