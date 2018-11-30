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
import com.outbound.dialer.util.EncryptRosterUtil;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.object.CallRecord;
import com.outbound.object.DbColumn;
import com.outbound.object.RosterInfo;
import com.outbound.object.RosterTemplateInfo;
import com.outbound.object.dao.CallRecordDAO;
import com.outbound.object.dao.RosterDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.dao.RosterTemplateDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;
import com.outbound.object.util.SqlConditionUtil;

@Path("/rosterRecord")
public class RosterRecordResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	
	private RosterDAO rosterDao = (RosterDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterDAO");

	private RosterInfoDAO rosterInfoDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterInfoDAO");
	
	private CallRecordDAO callRecordDao = (CallRecordDAO)ApplicationContextUtil.getApplicationContext()
			.getBean("CallRecordDAO");
	private RosterTemplateDAO rosterTemplateDao = (RosterTemplateDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterTemplateDAO");
	
	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getRosters(PageRequest request) {
					
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}
			List<RosterInfo> lists = new ArrayList<RosterInfo>();
			
			String condition = getCondition(request);
			
			List<RosterInfo> list = rosterInfoDao.getTRosterInfos2(condition, startpage,
					request.getPageNum());
			int count = rosterInfoDao.getTRosterInfoNum2(condition);
			
			/*
			List<RosterInfoHistory> history_lists = rosterHDao.getTRosterInfoHistorys2(condition, startpage,
					request.getPageNum());
			count += rosterHDao.getTRosterInfoHistoryNum2(condition);
			*/
			//lists.addAll(history_lists);
			lists.addAll(list);
			
			for(RosterInfo rosterInfo : lists){
				RosterTemplateInfo info = rosterTemplateDao.findByName(rosterInfo.getDomain(), rosterInfo.getTemplateName());
				if (info != null){
					List<DbColumn> hlist = gson.fromJson(info.getColumns(), MetricUtil.typeDBCol);
					EncryptRosterUtil.encryptRoster(rosterInfo, hlist);
				}
			}
			
			if(list!= null){
				for(RosterInfo rInfo: list){
					if(rInfo.getCallId() != null){
						CallRecord crecord = callRecordDao.queryRecordByUUID(rInfo.getCallId());
						if(crecord != null){
							rInfo.setIsRecord(crecord.getIsRecord());
							rInfo.setRecordPath(crecord.getRecordPath());
						}
					}
				}
			}
			
			responseUtil = setResponseUtil(1, "getRoster Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getRosters fail!", e);
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
		List<RosterInfo> lists  = rosterInfoDao.getTRosterInfos2(condition);
		//List<RosterInfoHistory> history_lists = rosterHDao.getTRosterInfoHistorys2(condition);
		
		//allList.addAll(history_lists);
		for(RosterInfo rosterInfo : lists){
			RosterTemplateInfo info = rosterTemplateDao.findByName(rosterInfo.getDomain(), rosterInfo.getTemplateName());
			if (info != null){
				List<DbColumn> hlist = gson.fromJson(info.getColumns(), MetricUtil.typeDBCol);
				EncryptRosterUtil.encryptRoster(rosterInfo, hlist);
			}
		}
		list.set(0, lists);
		
		try {
			XlsUtil.exportXls2(response,(List<Object>)list.get(0),(List<String>)list.get(1),(List<String>)list.get(2),(String)list.get(3),XlsUtil.getIsoStr("活动明细报表"));
			XlsUtil.map.remove(uuid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addRoster(RosterInfo template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = rosterInfoDao.createRosterInfo(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "add Roster Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "add Roster fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createRoster fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil updateRoster(RosterInfo template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = rosterInfoDao.updateRosterInfo(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "update Roster Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "update Roster fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateRoster fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteRoster(RosterInfo template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = rosterInfoDao.deleteRosterInfo(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "delete Roster Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete Roster fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteRoster fail!", e);
		}
		return responseUtil;
	}
	public  String getCondition(PageRequest request){
		String condition = " status=2";
		if(SqlConditionUtil.isAppendCondition(request.getDomain())){
			condition += " and domain= '"+ request.getDomain()+ "' ";
		}
		if(SqlConditionUtil.isAppendCondition(request.getStartTime())){
			condition += " and makeCallTime >='"+ request.getStartTime()+ "'";
		}
		if(SqlConditionUtil.isAppendCondition(request.getEndTime())){
			condition += " and makeCallTime <'"+ request.getEndTime()+ "'";
		}
		if(SqlConditionUtil.isAppendCondition(request.getUserName())){
			condition += " and (firstname like '%"+ request.getUserName()
			             + "%' or lastname like '%"+ request.getUserName()+ "%') ";
		}
		if(SqlConditionUtil.isAppendCondition(request.getActivityName())){
			condition += " and activityName ='"+ request.getActivityName()+ "'";
		}
		if(SqlConditionUtil.isAppendCondition(request.getBatchName())){
			condition += " and batchName ='"+ request.getBatchName()+ "'";
		}
		if(SqlConditionUtil.isAppendCondition(request.getPhoneNum())){
			condition += " and (phoneNum1 like '%"+ request.getPhoneNum()
			             + "%' or phoneNum2 like '%"+ request.getPhoneNum()
			             + "%' or phoneNum3 like '%"+request.getPhoneNum()+ "%')";
		}
		if(SqlConditionUtil.isAppendCondition(request.getCallRound())){
			condition += " and callRound ='"+ request.getCallRound()+ "'";
		}
		return condition;
	}
}
