package com.outbound.job;


public class JobUtil {

	public static void init(){
		CronJobManager.addJob("hour_report_job_0", HourMemClearJob.class, "0 0 * * * ?");
		CronJobManager.addJob("day_clear_job", DayClearJob.class, "0 1 0 * * ?");
        CronJobManager.addJob("batch_restat_job_0", BatchRestatJob.class, "0 0/1 * * * ?");
	}
}
