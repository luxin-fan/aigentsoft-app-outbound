package com.outbound.job;

import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

import com.outbound.object.util.TimeUtil;

public class SimpleJobManager
{
	private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
	private static String JOB_GROUP_NAME = "SIMPLE_JOBGROUP_NAME";
	private static String TRIGGER_GROUP_NAME = "SIMPLE_TRIGGERGROUP_NAME";

	public static void addJob(String jobName, @SuppressWarnings("rawtypes") Class cls, int waitSeconds, int timeRange,
			int repeat)
	{
		try
		{
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, cls);// 任务名，任务组，任务执行类
			// 触发器
			SimpleTrigger simpleTrigger = new SimpleTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组

			Date startTime = TimeUtil.nextGivenSecondDate(waitSeconds * 1000);
			simpleTrigger.setStartTime(startTime);// 开始运行时间
			simpleTrigger.setRepeatInterval(timeRange); // 运行间隔单位为毫秒
			simpleTrigger.setRepeatCount(repeat);
			simpleTrigger.setJobGroup(JOB_GROUP_NAME);
			simpleTrigger.setJobName(jobName);

			sched.addJob(jobDetail, true);
			sched.scheduleJob(simpleTrigger);
			// 启动
			if (!sched.isShutdown())
			{
				sched.start();
			}

			// System.out.println(sched.isShutdown());
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void modifyJobTime(String triggerName, String triggerGroupName, String time)
	{
		try
		{
			Scheduler sched = gSchedulerFactory.getScheduler();
			CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerName, triggerGroupName);
			if (trigger == null)
			{
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time))
			{
				CronTrigger ct = (CronTrigger) trigger;
				// 修改时间
				ct.setCronExpression(time);
				// 重启触发器
				sched.resumeTrigger(triggerName, triggerGroupName);
			}
		} 
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void removeJob(String jobName)
	{
		try
		{
			
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.pauseTrigger(jobName, TRIGGER_GROUP_NAME);// 停止触发器
			sched.unscheduleJob(jobName, TRIGGER_GROUP_NAME);// 移除触发器
			sched.deleteJob(jobName, JOB_GROUP_NAME);// 删除任务
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName)
	{
		try
		{
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.pauseTrigger(triggerName, triggerGroupName);// 停止触发器
			sched.unscheduleJob(triggerName, triggerGroupName);// 移除触发器
			sched.deleteJob(jobName, jobGroupName);// 删除任务
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void startJobs()
	{
		try
		{
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.start();
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void shutdownJobs()
	{
		try
		{
			Scheduler sched = gSchedulerFactory.getScheduler();
			if (!sched.isShutdown())
			{
				sched.shutdown();
			}
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}
