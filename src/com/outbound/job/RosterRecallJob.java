package com.outbound.job;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;
import com.outbound.dialer.util.RedisUtil;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.impl.util.CallUtil;
import com.outbound.impl.util.MakeCall;
import com.outbound.impl.util.ReqResponse;
import com.outbound.object.RosterInfo;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.TimeUtil;

public class RosterRecallJob implements Job {

	private Logger logger = Logger.getLogger(RosterRecallJob.class.getName());

	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterDAO");

	static Gson gson = new Gson();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		long currentTiem = System.currentTimeMillis();
		logger.info("#@## roster recall [" + context.getJobDetail().getName() + "] recall task start at "
				+ new Timestamp(currentTiem).toString());
		
		String jobname = context.getJobDetail().getName();
		jobname = jobname.replace("_recall_job", "");
		
		RosterInfo info = rosterDao.findById(jobname);
		MetricUtil.addCallOut(info);
		
		MakeCall mkcall = new MakeCall();
		mkcall.setAni(info.getAni());
		mkcall.setActivity_name(info.getActivityName());
		mkcall.setCaller(info.getMakeCallNum());
		mkcall.setCallee(info.getCallee());
		mkcall.setDomain(info.getDomain());
		mkcall.setTrunkGrp(info.getTrunkGroup());
		mkcall.setRosterinfo_id(""+info.getId());
		mkcall.setBatch_name(info.getBatchName());
		mkcall.setUui(info.getUui());
		
		ReqResponse result = CallUtil.makeCall(mkcall);
		if(result != null){
			RedisUtil.addRoster(info);
		}else{
			info.setMakeCallTime(TimeUtil.getCurrentTimeStr());
			info.setCallEndTime(TimeUtil.getCurrentTimeStr());
			info.setCallResult("外呼接口失败");
			info.setResultCode(12);
			info.setStatus(2);
			rosterDao.updateRosterInfo(info);
		}
	}
}
