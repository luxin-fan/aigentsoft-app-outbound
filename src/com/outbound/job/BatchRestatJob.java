package com.outbound.job;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.outbound.dialer.util.RedisUtil;
import com.outbound.impl.metric.BatchMetric;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.TimeUtil;

public class BatchRestatJob implements Job
{

	private Logger logger = Logger.getLogger(BatchRestatJob.class.getName());

	private RosterBatchInfoDAO batchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");

	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{

		long currentTiem = System.currentTimeMillis();
		logger.info("#@## batch restat job start at " + new Timestamp(currentTiem).toString());

		for (RosterBatchInfo batchInfo : MetricUtil.completeBatchs)
		{
			String batchName = batchInfo.getBatchId();

			if (RedisUtil.getBatchKeys(batchName) == 0)
			{

				logger.info("#@## " + batchName + ":" + batchInfo.getDomain() + " round " + batchInfo.getCallRound()
						+ " completed start to restat ");
				String bname = batchInfo.getBatchId() + ":" + batchInfo.getCallRound() + ":"
						+ batchInfo.getActivityName() + ":" + batchInfo.getDomain();
				BatchMetric metric = MetricUtil.batchMetrics.get(bname);
				if (metric != null)
				{
					batchInfo.setOutCallNum(metric.getOutCallNum());
					batchInfo.setAnswerCallNum(metric.getAnswerCallNum());
					batchInfo.setUnCallNum(metric.getUnCallNum());
					batchInfo.setDncNum(metric.getDncNum());
					batchInfo.setFail1Num(metric.getFail1Num());
					batchInfo.setFail2Num(metric.getFail2Num());
					batchInfo.setFail3Num(metric.getFail3Num());
					batchInfo.setFail4Num(metric.getFail4Num());
					batchInfo.setFail5Num(metric.getFail5Num());
					batchInfo.setFail6Num(metric.getFail6Num());
					batchInfo.setFail7Num(metric.getFail7Num());
					batchInfo.setFailOtherNum(metric.getFailOtherNum());
					batchInfo.setCompleteTime(TimeUtil.getCurrentTimeStr());
					batchInfo.setStatus(2);

					batchDao.updateRosterBatchInfo(batchInfo);
				} 
				else
				{
					logger.info("#@## " + bname + " not exist ! ");
					MetricUtil.removeBatchInfo(batchInfo);
				}
			}
			else
			{
				logger.info("#@## " + batchName + ":" + batchInfo.getDomain() + " not complete ! ");
			}
		}
	}
}
