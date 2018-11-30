package com.outbound.job;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.outbound.dialer.util.RosterUtil;
import com.outbound.dialer.util.XlsUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.AutoImportConfigModel;
import com.outbound.object.RosterInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.GsonFactory;
import com.outbound.object.util.TimeUtil;

public class RosterExportJob implements Job {

	private Logger logger = Logger.getLogger(RosterExportJob.class.getName());
	
	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");
	
	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterDAO");
	
	//呼叫结果回调接口
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		long currentTiem = System.currentTimeMillis();
		logger.info("#@## roster export [" + context.getJobDetail().getName() + "] export task start at "
				+ new Timestamp(currentTiem).toString());
		String jobname = context.getJobDetail().getName();
		jobname = jobname.replace("_back", "");
		
		ActivityInfo info = activityDao.findById(jobname);
		if(info != null){
			AutoImportConfigModel config = GsonFactory.getGson().fromJson(info.getActivityBackAddr()
					, AutoImportConfigModel.class);
			if(config != null){
				List<RosterInfo> rosters = rosterDao.findFinishRosters(info.getDomain(),info.getName());
				if(rosters != null){
					List<List<String>> exportRosters = new ArrayList<List<String>>();
					for(RosterInfo roster: rosters){
						exportRosters.add(RosterUtil.getRosterInfos(roster));							
					}
					rosterDao.updateFinishRosters(info.getDomain(),info.getName());
					try {
						String path = config.getContext().getPath() +"/[" + info.getName()+"_"
								+ info.getDomain() +"]_"+ TimeUtil.getExportCurrentTimeStr()+".xls";
						XlsUtil.exportRosterInfoXls(RosterUtil.getRosterHeaders(),
								exportRosters, path);
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}
		
	}
}
