package com.outbound.job;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


import com.outbound.impl.TaskContainer;
import com.outbound.impl.activity.RosterThread;
import com.outbound.impl.plan.PlanRosterThread;
import com.outbound.impl.util.AOUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;

public class CallRoundJob implements Job
{

	private Logger logger = Logger.getLogger(CallRoundJob.class.getName());
	
	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{

		long currentTiem = System.currentTimeMillis();
		logger.info("### roster [" + context.getJobDetail().getName() + "] callround job start at "
				+ new Timestamp(currentTiem).toString());
		String jobname = context.getJobDetail().getName();
		
		SimpleJobManager.removeJob(jobname);
		
		jobname = jobname.replace("&callround_job", "");

		String[] jobParams = jobname.split("&");
		String batchId = jobParams[0];
		String activtyName = jobParams[1];
		String domain = jobParams[2];

		/**
		 * 判断活动为计划任务还是普通任务
		 * @author zzj
		 */
		ActivityInfo ainfo = activityDao.finActivityInfoByActivityName(activtyName, domain);
		if(ainfo != null){
			
			if(ainfo.getActivityExecuteType() == 0){
				RosterThread rosterThread = TaskContainer.findRosterThread(AOUtil.genernateRosterThdId(domain, activtyName));
				
				if (rosterThread == null)
				{
					logger.error("### roster [" + context.getJobDetail().getName() + 
							"] callround job start not find roster thread:" + activtyName);
				}
				else
				{
					rosterThread.addRound(batchId);
				}
			}
			
			if(ainfo.getActivityExecuteType() == 1){
				PlanRosterThread planRosterThread = TaskContainer.findPlanRosterThread(AOUtil.genernateRosterThdId(domain, activtyName));
				
				if(planRosterThread == null){
					logger.error("### roster [" + context.getJobDetail().getName() + 
							"] callround job start not find plan roster thread:" + activtyName);
				}else{
					planRosterThread.addRound(batchId);
				}
			}
		}
	}
}
