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

import com.google.gson.Gson;
import com.outbound.common.BaseResource;
import com.outbound.common.PageRequest;
import com.outbound.common.Util;
import com.outbound.object.ActivityInfoHistory;
import com.outbound.object.dao.ActivityInfoHistoryDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/activityHistory")
public class ActivityHistoryResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private ActivityInfoHistoryDAO activityDao = (ActivityInfoHistoryDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoHistoryDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getActivityInfoHistorys(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if(startpage > 0){
				startpage --;
			}
			List<ActivityInfoHistory> lists = activityDao.getTActivityInfoHistorys
					(request.getDomain(), request,startpage,request.getPageNum());
			int count = activityDao.getTActivityInfoHistoryNum(request.getDomain(),request);
			responseUtil = setResponseUtil(1, "getActivityInfoHistory Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getActivityInfoHistorys fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addActivityInfoHistory(ActivityInfoHistory template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = activityDao.createActivityInfoHistory(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "add ActivityInfoHistory Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "add ActivityInfoHistory fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createActivityInfoHistory fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil updateActivityInfoHistory(ActivityInfoHistory template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = activityDao.updateActivityInfoHistory(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "update Activity History Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "update Activity History fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateActivityInfoHistory fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteActivityInfoHistory(ActivityInfoHistory template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = activityDao.deleteActivityInfoHistory(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "delete Activity History Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete Activity History fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteActivityInfoHistory fail!", e);
		}
		return responseUtil;
	}
}
