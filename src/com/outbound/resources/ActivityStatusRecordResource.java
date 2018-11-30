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

import org.hibernate.mapping.Array;

import com.google.gson.Gson;
import com.outbound.common.BaseResource;
import com.outbound.common.ExportResource;
import com.outbound.common.PageRequest;
import com.outbound.common.Util;
import com.outbound.common.XlsUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.ActivityInfoHistory;
import com.outbound.object.ActivityStatus;
import com.outbound.object.CallRecord;
import com.outbound.object.ConfigParam;
import com.outbound.object.RosterInfo;
import com.outbound.object.dao.CallRecordDAO;
import com.outbound.object.dao.ConfigParamDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;
import com.outbound.object.util.SqlConditionUtil;
import com.outbound.request.ConfigParamRequest;

@Path("/activityStatusRecord")
public class ActivityStatusRecordResource  extends BaseResource{
	private ConfigParamDAO configParamDao = (ConfigParamDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ConfigParamDAO");
	
	private CallRecordDAO callRecordDao = (CallRecordDAO)ApplicationContextUtil.getApplicationContext()
			.getBean("CallRecordDAO");
	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterInfoDAO");
	static Gson gson = new Gson();
	
	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getConfigParmas(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			String ivrNum = request.getIvrNum();
			if(!SqlConditionUtil.isAppendCondition(ivrNum)){
				responseUtil = setResponseUtil(1, "no ivr Num ",
						super.getMergeSumAndList(new ArrayList<>(), 0));
				return gson.toJson(responseUtil);
			}
			int validLength = 0;
			if(configParamDao.findValidLength(ivrNum)!=null){
				validLength = configParamDao.findValidLength(ivrNum).getValidLength();
			}
			
			List<RosterInfo> rosterInfoList = rosterDao.getRosterInfosByIvr(ivrNum,request.getStartTime(),request.getEndTime());
			
			ActivityStatus status = new ActivityStatus();
			status.setAllCount(rosterInfoList.size());
			status.setStartTime(request.getStartTime());
			status.setEndTime(request.getEndTime());
			int validCount = 0;
			int maxCallTime = 0;
			Long avgCallTime = 0L;
			Long totalCallTime = 0L;
			for(RosterInfo roster : rosterInfoList){
				if(roster.getAnswerCallTime() >= validLength){
					validCount++;
				}
				if(roster.getAnswerCallTime() > maxCallTime){
					maxCallTime = roster.getAnswerCallTime();
				}
				totalCallTime += roster.getCallTime();
			}
			if(totalCallTime!=0){
				avgCallTime = totalCallTime / rosterInfoList.size();
			}
			status.setValidCount(validCount);
			status.setMaxCallTime(maxCallTime);
			status.setAvgCallTime(avgCallTime);
			List<ActivityStatus> statusList = new ArrayList<>();
			statusList.add(status);
			responseUtil = setResponseUtil(1, "getConfigParmas Suc",
					super.getMergeSumAndList(rosterInfoList.size() == 0 ? new ArrayList<>() : statusList, rosterInfoList.size() == 0 ? 0:1));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
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
			
			String uuid = UUID.randomUUID().toString();
			
			responseUtil = setResponseUtil(1, "success", uuid);
			List<Object> list = new ArrayList<Object>();
			
			list.add(query);
			list.add(title);
			list.add(displayCondition);
			list.add(exportResource.getTable_title());
			XlsUtil.map.put(uuid,list);
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getActivityInfos fail!", e);
		}
		return gson.toJson(responseUtil);
	}
	
	@GET
	@Path("export/{uuid}")
	@Produces({ MediaType.APPLICATION_JSON })
	public void export(@Context HttpServletRequest request, @Context HttpServletResponse response,@PathParam("uuid") String uuid) throws Exception {
		
		List list = XlsUtil.map.get(uuid);
		PageRequest query = (PageRequest) list.get(0);
		String ivrNum = query.getIvrNum();
		
		int validLength = 0;
		if(configParamDao.findValidLength(ivrNum)!=null){
			validLength = configParamDao.findValidLength(ivrNum).getValidLength();
		}
		
		List<CallRecord> callRecordList = callRecordDao.getCallRecordsByIvr(ivrNum,query.getStartTime(),query.getEndTime());
		ActivityStatus status = new ActivityStatus();
		status.setAllCount(callRecordList.size());
		status.setStartTime(query.getStartTime());
		status.setEndTime(query.getEndTime());
		int validCount = 0;
		int maxCallTime = 0;
		Long avgCallTime = 0L;
		Long totalCallTime = 0L;
		for(CallRecord callRecord : callRecordList){
			if(callRecord.getAnswerCallTime() >= validLength){
				validCount++;
			}
			if(callRecord.getLastTime() > maxCallTime){
				maxCallTime = callRecord.getLastTime();
			}
			totalCallTime += callRecord.getAnswerCallTime();
		}
		if(totalCallTime!=0){
			avgCallTime = totalCallTime / callRecordList.size();
		}
		status.setValidCount(validCount);
		status.setMaxCallTime(maxCallTime);
		status.setAvgCallTime(avgCallTime);
		if(callRecordList.size() == 0){
			list.set(0, new ArrayList<ActivityStatus>());
		}else{
			List l = new ArrayList<ActivityStatus>();
			l.add(status);
			list.set(0, l);
		}
		
		try {
			XlsUtil.exportXls2(response,(List<Object>)list.get(0),(List<String>)list.get(1),(List<String>)list.get(2),(String)list.get(3),XlsUtil.getIsoStr("活动执行状态统计报表"));
			XlsUtil.map.remove(uuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
