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
import com.outbound.object.ActivityInfo;
import com.outbound.object.TrunkNumberPool;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.TrunkNumberPoolDAO;
import com.outbound.object.dao.TrunkPoolCorrDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/trunkpool")
public class TrunkNumberPoolResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private TrunkNumberPoolDAO trunkNumberPoolDao = (TrunkNumberPoolDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkNumberPoolDAO");
	
	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");
	
	private TrunkPoolCorrDAO trunkPoolCorrDao = (TrunkPoolCorrDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkPoolCorrDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getTrunkNumberPools(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if(startpage > 0){
				startpage --;
			}
			List<TrunkNumberPool> lists = trunkNumberPoolDao.getTTrunkNumberPools(request.getDomain(), startpage,
					request.getPageNum());
			int count = trunkNumberPoolDao.getTTrunkNumberPoolNum(request.getDomain());
			if(count>0){
				for(TrunkNumberPool pool : lists){
					int phonenum = trunkPoolCorrDao.getTTrunkPoolNum(request.getDomain(), pool.getName());
					pool.setPhonenumCount(phonenum);
				}
			}
			responseUtil = setResponseUtil(1, "getTrunkNumber Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getTrunkNumberPools fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addTrunkPool(TrunkNumberPool trunkPool) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = trunkNumberPoolDao.createTrunkNumberPool(trunkPool);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "add trunkNumberPool Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "add trunkNumberPool fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createTrunkNumberPool fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil updateTrunkNumberPool(TrunkNumberPool pool) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = trunkNumberPoolDao.updateTrunkNumberPool(pool);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "update Trunk Number Pool Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "update Trunk Number Pool fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateTrunkNumberPool fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteTrunkNumberPool(TrunkNumberPool pool) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			
			ActivityInfo info = activityDao.findActivityInfoByTrunk(pool.getDomain(), pool.getName());
			if(info != null){
				responseUtil = setResponseUtil(0, "中继组关联活动，无法删除", null);
				return responseUtil;
			}
			boolean ret = trunkNumberPoolDao.deleteTrunkNumberPool(pool);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "delete Trunk Number Pool Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete Trunk Number Pool fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteTrunkNumberPool fail!", e);
		}
		return responseUtil;
	}
	
	@POST
	@Path("check")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil checkTrunkName(CheckRequest number) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			
			boolean ret = trunkNumberPoolDao.checkPoolName(number.getName(), number.getDomain());
			responseUtil = setResponseUtil(1, ret == true ? "true" : "false", null);
		} catch (Exception e) {
			Util.error(this, "check trunk name fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}
}
