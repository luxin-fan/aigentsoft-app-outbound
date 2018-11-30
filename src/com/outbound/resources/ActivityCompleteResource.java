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
import com.outbound.object.ActivityCompleteConfig;
import com.outbound.object.dao.ActivityCompleteConfigDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/completeCondition")
public class ActivityCompleteResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private ActivityCompleteConfigDAO activityDao = (ActivityCompleteConfigDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityCompleteConfigDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getActivityCompleteConfigs(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if(startpage > 0){
				startpage --;
			}
			List<ActivityCompleteConfig> lists = activityDao.getTActivityCompleteConfigs
					(request.getDomain(), startpage,request.getPageNum());
			int count = activityDao.getTActivityCompleteConfigNum(request.getDomain());
			responseUtil = setResponseUtil(1, "getActivityCompleteConfig Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getActivityCompleteConfigs fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addActivityCompleteConfig(ActivityCompleteConfig template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = activityDao.createActivityCompleteConfig(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "add ActivityCompleteConfig Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "add ActivityCompleteConfig fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createActivityCompleteConfig fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil updateActivityCompleteConfig(ActivityCompleteConfig template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = activityDao.updateActivityCompleteConfig(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "update Activity History Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "update Activity History fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateActivityCompleteConfig fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteActivityCompleteConfig(ActivityCompleteConfig template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = activityDao.deleteActivityCompleteConfig(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "delete Activity History Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete Activity History fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteActivityCompleteConfig fail!", e);
		}
		return responseUtil;
	}
}
