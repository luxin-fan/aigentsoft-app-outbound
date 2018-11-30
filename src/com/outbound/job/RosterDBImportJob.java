package com.outbound.job;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;

public class RosterDBImportJob implements Job {

	private Logger logger = Logger.getLogger(RosterDBImportJob.class.getName());

	// +"——"+context.getJobDetail().getName()
	// config = GsonFactory.getGson().fromJson(dncTemplate.getImportPath(),
	// ImportConfigModel.class);

	static Gson gson = new Gson();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		long currentTiem = System.currentTimeMillis();
		logger.info("#@## roster db import [" + context.getJobDetail().getName() + "] import task start at "
				+ new Timestamp(currentTiem).toString());
	}

}
