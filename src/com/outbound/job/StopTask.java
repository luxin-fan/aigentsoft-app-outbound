package com.outbound.job;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.outbound.impl.TaskContainer;
import com.outbound.impl.activity.ActivityThread;
/**
 * 增加定时任务  到时间停止活动
 * @author zzj
 *
 */
public class StopTask implements Job{

	private Logger logger = Logger.getLogger(StopTask.class);
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String jobname = context.getJobDetail().getName();
		
		ActivityThread aThread = TaskContainer.findActivityThread(jobname);
		if(aThread != null){
			aThread.stopTask();;
		}else{
			logger.info("#######ActivityThread " + jobname + " thread not exits! ");
		}
	}

}
