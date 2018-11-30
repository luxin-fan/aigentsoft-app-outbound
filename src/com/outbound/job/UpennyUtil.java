package com.outbound.job;

import java.util.List;

import com.outbound.impl.auto.AutoCallThread;
import com.outbound.impl.metric.ActivityMetric;
import com.outbound.impl.metric.BatchMetric;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.OutboundPolicyInfo;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.OutboundPolicyInfoDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.TimeUtil;

public class UpennyUtil {

	private static ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");
	
	private static OutboundPolicyInfoDAO outboundPolicyInfoDao = (OutboundPolicyInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("OutboundPolicyInfoDAO");
	
	private static RosterBatchInfoDAO rosterBatchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");
	
	public static void init(){
		List<ActivityInfo> infos = activityDao.getAllActivityInfos();
		if(infos != null){
			for(ActivityInfo info : infos ){
				if(info.getStatus() ==1 || info.getStatus()==2){
					info.setActivityExecuteTime(TimeUtil.getCurrentTimeStr());
					activityDao.updateActivityInfo(info);
					
					OutboundPolicyInfo pinfo = outboundPolicyInfoDao.getTOutboundPolicyInfos(info.getDomain(), info.getPolicyName());
					AutoCallThread aThread = new AutoCallThread("Auto",info,pinfo);
					aThread.setStatus(info.getStatus());
					aThread.start();
					
					String name = info.getName() +":" + info.getDomain();
					ActivityMetric metric = MetricUtil.actMetrics.get(name);
					if(metric == null){
						metric = new ActivityMetric();
						MetricUtil.actMetrics.put(name, metric);
					}
					
					metric.setActivityName(info.getName());
					metric.setOutCallNum(info.getOutCallNum());
					metric.setAnswerCallNum(info.getAnswerCallNum());
				}
			}
		}
		
		List<RosterBatchInfo> binfos = rosterBatchDao.getTodayRosterBatchInfos();
		for(RosterBatchInfo ibnfo : binfos ){
			String bname = ibnfo.getBatchId()+":" + ibnfo.getActivityName() +":" + ibnfo.getDomain();
			BatchMetric metric = MetricUtil.batchMetrics.get(bname);
			if(metric == null){
				metric = new BatchMetric();
				MetricUtil.batchMetrics.put(bname, metric);
			}
			metric.setActivityName(ibnfo.getActivityName());
			metric.setDomain(ibnfo.getDomain());
			metric.setBatchName(ibnfo.getBatchId());
			metric.setRoterNum(ibnfo.getRoterNum());
			metric.setAnswerCallNum(ibnfo.getAnswerCallNum());
			metric.setFail1Num(ibnfo.getFail1Num());
			metric.setFail2Num(ibnfo.getFail2Num());
			metric.setFail3Num(ibnfo.getFail3Num());
			metric.setFail4Num(ibnfo.getFail4Num());
			metric.setFail5Num(ibnfo.getFail5Num());
			metric.setFailOtherNum(ibnfo.getFailOtherNum());
			metric.setOutCallNum(ibnfo.getOutCallNum());
			metric.setStatus(ibnfo.getStatus());
			
		}
		
	}
}
