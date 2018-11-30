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
import com.outbound.object.ConfigParam;
import com.outbound.object.OutboundPolicyInfo;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.ConfigParamDAO;
import com.outbound.object.dao.OutboundPolicyInfoDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;
import com.outbound.object.util.SqlConditionUtil;

@Path("/rosterBatchRecord")
public class RosterBatchRecordResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private RosterBatchInfoDAO rosterBatchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");
	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterInfoDAO");
	private ConfigParamDAO configParamDao = (ConfigParamDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ConfigParamDAO");

	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");
	
	private OutboundPolicyInfoDAO outboundPolicyInfoDao = (OutboundPolicyInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("OutboundPolicyInfoDAO");
	
	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getAllRosterBatchs(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}
			String condition = getCondition(request);
			List<RosterBatchInfo> lists = rosterBatchDao.getTRosterBatchInfos2(condition,
					 startpage, request.getPageNum());
			if(lists != null){
				for(RosterBatchInfo rBinfo : lists){
					ActivityInfo aInfo = activityDao.getTActivityInfos(rBinfo.getDomain(), rBinfo.getTemplateName());
					if(aInfo != null){
						OutboundPolicyInfo info = outboundPolicyInfoDao.getTOutboundPolicyInfos(rBinfo.getDomain(), aInfo.getPolicyName());
						if(info == null){
							continue;
						}
						String[] steps = info.getCallAnswerStep().split("[|]");
						int effectiveTime = 0;
						if(steps.length >=3 ){
							String dstName = steps[2];
							ConfigParam pvalue = configParamDao.findByName(rBinfo.getDomain(), dstName);
							if(pvalue != null){
								effectiveTime = pvalue.getValidLength();
							}
						}
						int effectNum = rosterDao.getEffectAnswerNums(rBinfo.getDomain(), rBinfo.getBatchId(), 
								effectiveTime);
						rBinfo.setEffectiveNum(effectNum);
					}
				}
			}
			int count = rosterBatchDao.getTRosterBatchInfoNum2(condition);
			responseUtil = setResponseUtil(1, "getRosterBatch Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getRosterBatchs fail!", e);
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
		String condition = getCondition(query);
		List<Object> allList = new ArrayList<Object>();
		List<RosterBatchInfo> lists = rosterBatchDao.getTRosterBatchInfos2(condition);
		if(lists != null){
			for(RosterBatchInfo rBinfo : lists){
				ActivityInfo aInfo = activityDao.getTActivityInfos(rBinfo.getDomain(), rBinfo.getTemplateName());
				if(aInfo != null){
					OutboundPolicyInfo info = outboundPolicyInfoDao.getTOutboundPolicyInfos(rBinfo.getDomain(), aInfo.getPolicyName());
					if(info == null){
						continue;
					}
					String[] steps = info.getCallAnswerStep().split("[|]");
					int effectiveTime = 0;
					if(steps.length >=3 ){
						String dstName = steps[2];
						ConfigParam pvalue = configParamDao.findByName(rBinfo.getDomain(), dstName);
						if(pvalue != null){
							effectiveTime = pvalue.getValidLength();
						}
					}
					int effectNum = rosterDao.getEffectAnswerNums(rBinfo.getDomain(), rBinfo.getBatchId(), 
							effectiveTime);
					rBinfo.setEffectiveNum(effectNum);
				}
			}
		}
		
		
		allList.addAll(lists);
		list.set(0, allList);
		
		try {
			XlsUtil.exportXls2(response,(List<Object>)list.get(0),(List<String>)list.get(1),(List<String>)list.get(2),(String)list.get(3),XlsUtil.getIsoStr("批次统计报表"));
			XlsUtil.map.remove(uuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@POST
	@Path("listUncall")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getUncallRosterBatchs(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}
			List<RosterBatchInfo> lists = rosterBatchDao.getUnCallRosterBatchInfos(request.getDomain(),
					request.getTemplateName(),request.getBatchName(), startpage, request.getPageNum());
			int count = rosterBatchDao.getUncallRosterBatchInfoNum(request.getDomain(),
					request.getTemplateName());
			responseUtil = setResponseUtil(1, "getRosterBatch Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getRosterBatchs fail!", e);
		}
		return gson.toJson(responseUtil);
	}
	
	public  String getCondition(PageRequest request){
		String condition = "";
		if(SqlConditionUtil.isAppendCondition(request.getDomain())){
			condition += " domain= '"+ request.getDomain()+ "' ";
		}
		
		if(SqlConditionUtil.isAppendCondition(request.getActivityName())){
			condition += " and activityName= '"+ request.getActivityName()+ "'";
		}
		
		if(SqlConditionUtil.isAppendCondition(request.getStartTime())){
			condition += " and startTime >='"+ request.getStartTime()+ "'";
		}
		
		if(SqlConditionUtil.isAppendCondition(request.getEndTime())){
			condition += " and startTime <'"+ request.getEndTime()+ "'";
		}
		
		if(SqlConditionUtil.isAppendCondition(request.getCallRound())){
			condition += " and callRound ='"+ request.getCallRound()+ "'";
		}
		
		if(SqlConditionUtil.isAppendCondition(request.getBatchName())){
			condition += " and batchId ='"+ request.getBatchName()+ "'";
		}
		return condition;
	}
}
