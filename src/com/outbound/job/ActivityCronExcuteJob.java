package com.outbound.job;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.outbound.impl.TaskContainer;
import com.outbound.impl.plan.PlanRosterThread;

public class ActivityCronExcuteJob implements Job {

	private Logger logger = Logger.getLogger(ActivityCronExcuteJob.class.getName());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		long currentTiem = System.currentTimeMillis();
		logger.info("#@## plan roster cron [" + context.getJobDetail().getName() + "] import task start at "
				+ new Timestamp(currentTiem).toString());
		String jobname = context.getJobDetail().getName();
		
		PlanRosterThread pAuto = TaskContainer.findPlanRosterThread(jobname);
		if(pAuto != null){
			pAuto.getUncallBatch();
		}else{
			logger.info("#@## plan roster cron [" + context.getJobDetail().getName() + "] thread not exits! ");
		}
	}
}
