package com.outbound.job;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HourMemClearJob implements Job {

	private Logger logger = Logger.getLogger(HourMemClearJob.class.getName());

	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		long currentTiem = System.currentTimeMillis();
		logger.info("#@## Hour men task start at " + new Timestamp(currentTiem).toString());

		long used = gcMemory();
		if (used >= 300) {
			logger.info("### used memory " + used + " start to gc !");
			System.gc();
			logger.info("## after gc info ");
			gcMemory();
		}

	}

	public long gcMemory() {

		long freeMemory = Runtime.getRuntime().freeMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long maxsMemory = Runtime.getRuntime().maxMemory();

		freeMemory = freeMemory / 1024 / 1024;
		totalMemory = totalMemory / 1024 / 1024;
		maxsMemory = maxsMemory / 1024 / 1024;

		logger.info("### free mem " + freeMemory + "M | " + "total mem " + totalMemory + "M | " + "max mem "
				+ maxsMemory + "M");

		long usedMem = totalMemory - freeMemory;
		logger.info("#### used mem " + usedMem + "M");

		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup topGroup = group;
		// 遍历线程组树，获取根线程组
		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}
		// 激活的线程数加倍
		int estimatedSize = topGroup.activeCount() * 2;
		Thread[] slackList = new Thread[estimatedSize];
		// 获取根线程组的所有线程
		int actualSize = topGroup.enumerate(slackList);
		// copy into a list that is the exact size
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);
		logger.info("#### Thread list size == " + list.length);
		for (Thread thread : list) {
			logger.info(thread.getName() + " state:" + thread.getState().toString());
		}

		return usedMem;
	}

}
