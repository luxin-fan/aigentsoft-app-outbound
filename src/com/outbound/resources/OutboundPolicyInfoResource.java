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
import com.outbound.common.CheckRequest;
import com.outbound.common.PageRequest;
import com.outbound.common.Util;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.OutboundPolicyInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.OutboundPolicyInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/outboundPolicy")
public class OutboundPolicyInfoResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private OutboundPolicyInfoDAO outboundPolicyInfoDao = (OutboundPolicyInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("OutboundPolicyInfoDAO");
	
	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getOutboundPolicyInfos(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if(startpage > 0){
				startpage --;
			}
			List<OutboundPolicyInfo> lists = outboundPolicyInfoDao.getTOutboundPolicyInfos
					(request.getDomain(), startpage,request.getPageNum(),request.getPolicyName());
			for (OutboundPolicyInfo tInfo : lists) {
				List<String> list = gson.fromJson(tInfo.getTimeRangeStr(), MetricUtil.typeList);
				tInfo.setTimeRange(list);
				List<String> list3 = gson.fromJson(tInfo.getResProcessStr(), MetricUtil.typeList);
				tInfo.setCallResultList(list3);
			}
			int count = outboundPolicyInfoDao.getTOutboundPolicyInfoNum(request.getDomain(),request.getPolicyName());
			responseUtil = setResponseUtil(1, "getOutboundPolicyInfo Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getOutboundPolicyInfos fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addOutboundPolicyInfo(OutboundPolicyInfo template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			template.setTimeRangeStr(gson.toJson(template.getTimeRange()));
			template.setResProcessStr(gson.toJson(template.getCallResultList()));
			boolean ret = outboundPolicyInfoDao.createOutboundPolicyInfo(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "add OutboundPolicyInfo Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "add OutboundPolicyInfo fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createOutboundPolicyInfo fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil updateOutboundPolicyInfo(OutboundPolicyInfo template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			template.setTimeRangeStr(gson.toJson(template.getTimeRange()));
			template.setResProcessStr(gson.toJson(template.getCallResultList()));
			boolean ret = outboundPolicyInfoDao.updateOutboundPolicyInfo(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "update Outbound Policy Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "update Outbound Policy fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateOutboundPolicyInfo fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteOutboundPolicyInfo(OutboundPolicyInfo template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			ActivityInfo info = activityDao.findActivityInfoByPolicy(template.getName());
			if(info != null){
				responseUtil = setResponseUtil(0, "策略已关联活动，不能删除", null);
				return responseUtil;
			}
			boolean ret = outboundPolicyInfoDao.deleteOutboundPolicyInfo(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "delete Outbound Policy Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete Outbound Policy fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteOutboundPolicyInfo fail!", e);
		}
		return responseUtil;
	}
	
	@POST
	@Path("check")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil checkName(CheckRequest number) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = outboundPolicyInfoDao.checkName(number.getName(), number.getDomain());
			responseUtil = setResponseUtil(1, ret == true ? "true" : "false", null);
		} catch (Exception e) {
			Util.error(this, "check trunk name fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}
}
