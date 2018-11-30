package com.outbound.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import com.google.gson.Gson;
import com.outbound.common.BaseResource;
import com.outbound.common.ExportResource;
import com.outbound.common.PageRequest;
import com.outbound.common.Util;
import com.outbound.common.XlsUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.ActivityInfoHistory;
import com.outbound.object.RosterBatchMetric;
import com.outbound.object.RosterInfoMetric;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.ActivityInfoHistoryDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;
import com.outbound.object.util.SqlConditionUtil;

@Path("/activityRecord")
public class ActivityRecordResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");
	
	private RosterBatchInfoDAO rosterBatchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");
	
	private ActivityInfoHistoryDAO activityHistoryDao = (ActivityInfoHistoryDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoHistoryDAO");
	
	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterInfoDAO");
	
	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getActivityInfos(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if(startpage > 0){
				startpage --;
			}
			List<Object> allList = new ArrayList<Object>();
			String condition = getCondition(request);
			
			List<ActivityInfo> lists = activityDao.getTActivityInfos2(condition, startpage,request.getPageNum());
			
			int count = activityDao.getTActivityInfoNum2(condition);
			
			List<ActivityInfoHistory> history_lists = activityHistoryDao.getTActivityInfoHistorys2(condition, startpage,request.getPageNum());
			count += activityHistoryDao.getTActivityInfoHistoryNum2(condition);
			if(lists != null){
				for(ActivityInfo info : lists){
					
					RosterBatchMetric b_metric = rosterBatchDao.getRosterBatchMetric(info.getRosterTemplateName(), info.getDomain());
					int tBatchNum = b_metric.getTotalRosterBatchNum();
					int cBatchNum = b_metric.getFinishedRosterBatchNum();
					
					RosterInfoMetric r_metric = rosterDao.getRosterMetric(info.getDomain(), info.getRosterTemplateName());
					int tRosterNum = r_metric.getAllRosterNum();
					int oRosterNum = r_metric.getTodayOutCallNum();
					int aRosterNum = r_metric.getTodayOutCallAnswerNum();
					int cRosterNum = r_metric.getAllCalledRosterNum();
					
					String avgCallTime = rosterDao.getAvgOutCallAnswerTime(info.getDomain(), info.getRosterTemplateName());
					
					info.setBatchNum(tBatchNum);
					info.setCompleteBatchNum(cBatchNum);
					info.setRosterNum(tRosterNum);
					info.setOutCallRosterNum(cRosterNum);
					info.setOutCallNum(oRosterNum);
					info.setAnswerCallNum(aRosterNum);
					info.setOutCallTimeNum(avgCallTime);
				}
			}
			
			if(history_lists != null){
				for(ActivityInfoHistory info : history_lists){
					RosterBatchMetric b_metric = rosterBatchDao.getRosterBatchMetric(info.getRosterTemplateName(), info.getDomain());
					int tBatchNum = b_metric.getTotalRosterBatchNum();
					int cBatchNum = b_metric.getFinishedRosterBatchNum();
					
					RosterInfoMetric r_metric = rosterDao.getRosterMetric(info.getDomain(), info.getRosterTemplateName());
					int tRosterNum = r_metric.getAllRosterNum();
					int oRosterNum = r_metric.getTodayOutCallNum();
					int aRosterNum = r_metric.getTodayOutCallAnswerNum();
					int cRosterNum = r_metric.getAllCalledRosterNum();
					String avgCallTime = rosterDao.getAvgOutCallAnswerTime(info.getDomain(), info.getRosterTemplateName());
					
					info.setBatchNum(tBatchNum);
					info.setCompleteBatchNum(cBatchNum);
					info.setRosterNum(tRosterNum);
					info.setOutCallRosterNum(cRosterNum);
					info.setOutCallNum(oRosterNum);
					info.setAnswerCallNum(aRosterNum);
					info.setOutCallTimeNum(avgCallTime);
				}
			}
			
			allList.addAll(history_lists);
			allList.addAll(lists);
			responseUtil = setResponseUtil(1, "getActivityInfo Suc",
					super.getMergeSumAndList(allList == null ? new ArrayList<>() : allList, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getActivityInfos fail!", e);
		}
		return gson.toJson(responseUtil);
	}
	
	@POST
	@Path("export")
	@Produces({ MediaType.APPLICATION_JSON })
	public String export(@Context HttpServletResponse response,ExportResource exportResource) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			PageRequest query = exportResource.getPageRequest();
			
			List<String> title = exportResource.getTitle();
			
			List<String> displayCondition = exportResource.getCondition();
			
			String table_title = exportResource.getTable_title();
			
			String uuid = UUID.randomUUID().toString();
			
			responseUtil = setResponseUtil(1, "success", uuid);
			List<Object> list = new ArrayList<Object>();
			
			list.add(query);
			list.add(title);
			list.add(displayCondition);
			list.add(table_title);
			XlsUtil.map.put(uuid,list);
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getActivityInfos fail!", e);
		}
		return gson.toJson(responseUtil);
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Path("export/{uuid}")
	@Produces({ MediaType.APPLICATION_JSON })
	public void export(@Context HttpServletRequest request, @Context HttpServletResponse response,@PathParam("uuid") String uuid) throws Exception {
		List list = XlsUtil.map.get(uuid);
		PageRequest query = (PageRequest) list.get(0);
		String condition = getCondition(query);
		List<Object> allList = new ArrayList<Object>();
		List<ActivityInfo> lists = activityDao.getTActivityInfos2(condition);
		List<ActivityInfoHistory> history_lists = activityHistoryDao.getTActivityInfoHistorys2(condition);
		if(lists != null){
			for(ActivityInfo info : lists){
				
				RosterBatchMetric b_metric = rosterBatchDao.getRosterBatchMetric(info.getRosterTemplateName(), info.getDomain());
				int tBatchNum = b_metric.getTotalRosterBatchNum();
				int cBatchNum = b_metric.getFinishedRosterBatchNum();
				
				RosterInfoMetric r_metric = rosterDao.getRosterMetric(info.getDomain(), info.getRosterTemplateName());
				int tRosterNum = r_metric.getAllRosterNum();
				int oRosterNum = r_metric.getTodayOutCallNum();
				int aRosterNum = r_metric.getTodayOutCallAnswerNum();
				int cRosterNum = r_metric.getAllCalledRosterNum();
				
				String avgCallTime = rosterDao.getAvgOutCallAnswerTime(info.getDomain(), info.getRosterTemplateName());
				
				info.setBatchNum(tBatchNum);
				info.setCompleteBatchNum(cBatchNum);
				info.setRosterNum(tRosterNum);
				info.setOutCallRosterNum(cRosterNum);
				info.setOutCallNum(oRosterNum);
				info.setAnswerCallNum(aRosterNum);
				info.setOutCallTimeNum(avgCallTime);
			}
		}
		if(history_lists != null){
			for(ActivityInfoHistory info : history_lists){
			
				RosterBatchMetric b_metric = rosterBatchDao.getRosterBatchMetric(info.getRosterTemplateName(), info.getDomain());
				int tBatchNum = b_metric.getTotalRosterBatchNum();
				int cBatchNum = b_metric.getFinishedRosterBatchNum();
				
				RosterInfoMetric r_metric = rosterDao.getRosterMetric(info.getDomain(), info.getRosterTemplateName());
				int tRosterNum = r_metric.getAllRosterNum();
				int oRosterNum = r_metric.getTodayOutCallNum();
				int aRosterNum = r_metric.getTodayOutCallAnswerNum();
				int cRosterNum = r_metric.getAllCalledRosterNum();
				
				String avgCallTime = rosterDao.getAvgOutCallAnswerTime(info.getDomain(), info.getRosterTemplateName());
				
				info.setBatchNum(tBatchNum);
				info.setCompleteBatchNum(cBatchNum);
				info.setRosterNum(tRosterNum);
				info.setOutCallRosterNum(cRosterNum);
				info.setOutCallNum(oRosterNum);
				info.setAnswerCallNum(aRosterNum);
				info.setOutCallTimeNum(avgCallTime);
			}
		}
		
		allList.addAll(lists);
		allList.addAll(history_lists);
		list.set(0, allList);
		
		try {
			XlsUtil.exportXls2(response,(List<Object>)list.get(0),(List<String>)list.get(1),(List<String>)list.get(2),(String)list.get(3),XlsUtil.getIsoStr("活动统计报表"));
			XlsUtil.map.remove(uuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public  String getCondition(PageRequest request){
		String condition = "";
		if(SqlConditionUtil.isAppendCondition(request.getDomain())){
			condition += " domain= '"+ request.getDomain()+ "' ";
		}
		if(SqlConditionUtil.isAppendCondition(request.getActivityBeginTime())){
			condition += " and beginDatetime >='"+ request.getActivityBeginTime()+ "'";
		}
		if(SqlConditionUtil.isAppendCondition(request.getActivityEndTime())){
			condition += " and beginDatetime <'"+ request.getActivityEndTime()+ "'";
		}
		if(SqlConditionUtil.isAppendCondition(request.getActivityName())){
			condition += " and name ='"+ request.getActivityName()+ "'";
		}
		if(SqlConditionUtil.isAppendCondition(request.getActivityStatus())){
			condition += " and status ='"+ request.getActivityStatus()+ "'";
		}
		return condition;
	}
}
