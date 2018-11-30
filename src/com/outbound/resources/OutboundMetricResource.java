package com.outbound.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.BeanUtils;

import com.google.gson.Gson;
import com.outbound.common.BaseResource;
import com.outbound.common.PageRequest;
import com.outbound.common.Util;
import com.outbound.dialer.util.RedisUtil;
import com.outbound.impl.metric.ActivityMetric;
import com.outbound.impl.metric.BatchMetric;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.RosterBatchMetric;
import com.outbound.object.RosterInfoMetric;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/metric")
public class OutboundMetricResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	static Gson gson = new Gson();

	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	private RosterBatchInfoDAO batchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");
	
	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterInfoDAO");

	@POST
	@Path("activitys")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getActivityMetrics(PageRequest request) {

		//long sTime = System.currentTimeMillis();
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}
			List<ActivityInfo> lists;
			int count = 0;
			if(request.getActivityName()!= null && request.getActivityName().length() > 0){
				 lists = activityDao.getTActivityInfos2(request.getDomain(), startpage,
						request.getPageNum(), request.getActivityName());
			}else{
				 lists = activityDao.getTActivityInfos(request.getDomain(), startpage,
						request.getPageNum());
			}
			count = lists.size();
			//int count = activityDao.getTActivityInfoNum(request.getDomain(), request.getActivityName());
			//logger.info("## step 1 cost " + (System.currentTimeMillis() -sTime) + "ms");
			//sTime = System.currentTimeMillis();
			List<ActivityMetric> metrics = new ArrayList<ActivityMetric>();
			if (count > 0) {
				for (ActivityInfo info : lists) {
					String name = info.getName() + ":" + info.getDomain();
					ActivityMetric metric = MetricUtil.actMetrics.get(name);
					if (metric == null) {
						metric = new ActivityMetric();
					}
					metric.setActivityName(info.getName());
					metric.setMaxCurrent(info.getMaxCapacity());
					metric.setTemplateName(info.getRosterTemplateName());
					metric.setDomain(info.getDomain());
					metric.setStatus(info.getStatus());
					metric.setStartTime(info.getBeginDatetime());
					metric.setEndTime(info.getEndDatetime());
					metric.setCallRate(RedisUtil.getKeys(metric.getActivityName()));
					
					RosterBatchMetric b_metric = batchDao.getRosterBatchMetric(info.getRosterTemplateName(), info.getDomain());
					int tBatchNum = b_metric.getTotalRosterBatchNum();
					int cBatchNum = b_metric.getFinishedRosterBatchNum();
					int todayBatchNum = b_metric.getTodayRosterBatchNum();
					
					RosterInfoMetric r_metric = rosterDao.getRosterMetric(info.getDomain(), info.getRosterTemplateName());
					int tRosterNum = r_metric.getAllRosterNum();
					int todayRosterNum = r_metric.getTodayAllRosterNum();
					int oRosterNum = r_metric.getTodayOutCallNum();
					int aRosterNum = r_metric.getTodayOutCallAnswerNum();
					
					metric.setBatchNum(tBatchNum);
					metric.setCompleteBatchNum(cBatchNum);
					metric.setRoterNum(tRosterNum);
					metric.setCompleteBatchNumDay(todayBatchNum);
					metric.setRosterNumDay(todayRosterNum);
					metric.setOutCallNum(oRosterNum);
					metric.setAnswerCallNum(aRosterNum);
					
					metrics.add(metric);
				}
			}
			//logger.info("## step end cost " + (System.currentTimeMillis() -sTime) + "ms");
			//logger.info("## ---------------------------- ");
			responseUtil = setResponseUtil(1, "getActivityInfo Suc",
					super.getMergeSumAndList(metrics == null ? new ArrayList<>() : metrics, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getActivityInfos fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("batch")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getBatchDetail(PageRequest request) {

		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}
			List<RosterBatchInfo> lists = batchDao.getTRosterBatchInfos(request.getDomain(), request.getTemplateName(),
					request.getBatchName(), request.getCallRound(), startpage, request.getPageNum());
			int count = batchDao.getTRosterBatchInfoNum(request.getDomain(), request.getTemplateName(), request.getBatchName(), request.getCallRound());
			
			List<BatchMetric> metrics = new ArrayList<BatchMetric>();
			if (count > 0) {
				for (RosterBatchInfo info : lists) {
					String bname = info.getBatchId() + ":"+ info.getCallRound() +":" + request.getActivityName() + ":" + info.getDomain();
					BatchMetric metric = MetricUtil.batchMetrics.get(bname);
					if (metric == null) {
						metric = new BatchMetric();
					}

					BatchMetric newBatchMetric = new BatchMetric();
					BeanUtils.copyProperties(metric, newBatchMetric);
					metric.setRound(info.getCallRound());
					metric.setRoterNum(info.getRoterNum());
					metric.setBatchName(info.getBatchId());
					metric.setDomain(info.getDomain());
					metric.setStatus(info.getStatus());
					metric.setStartTime(info.getCompleteTime());
					metrics.add(metric);
				}
			}
			responseUtil = setResponseUtil(1, "getActivityInfo Suc",
					super.getMergeSumAndList(metrics == null ? new ArrayList<>() : metrics, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getActivityInfos fail!", e);
		}
		return gson.toJson(responseUtil);
	}

}
