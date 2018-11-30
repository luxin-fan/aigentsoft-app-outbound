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
import com.outbound.object.TrunkNumber;
import com.outbound.object.dao.TrunkNumberDAO;
import com.outbound.object.dao.TrunkPoolCorrDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/trunk")
public class TrunkNumberResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private TrunkNumberDAO trunkNumberDao = (TrunkNumberDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkNumberDAO");
	
	private TrunkPoolCorrDAO trunkPoolCorrDao = (TrunkPoolCorrDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkPoolCorrDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getTrunkAniPools(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if(startpage > 0){
				startpage --;
			}
			List<TrunkNumber> lists = trunkNumberDao.getTTrunkNumbers(request.getDomain(), startpage,
					request.getPageNum());
			int count = trunkNumberDao.getTTrunkNumberNum(request.getDomain());
			responseUtil = setResponseUtil(1, "getTrunkNumber Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getTrunkAniPools fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addAniPool(TrunkNumber trunkNumber) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = trunkNumberDao.createTrunkNumber(trunkNumber);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "add trunkNumber Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "add trunkNumber fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createTrunkAniPool fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil updateTrunkAniPool(TrunkNumber number) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = trunkNumberDao.updateTrunkNumber(number);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "update Trunk Number Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "update Trunk Number fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateTrunkAniPool fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteTrunkAniPool(TrunkNumber number) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = trunkNumberDao.deleteTrunkNumber(number);
			if (ret == true) {
				trunkPoolCorrDao.deleteByNumber(number.getDisplayNum(), number.getDomain());
				responseUtil = setResponseUtil(1, "delete Trunk Number Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete Trunk Number fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteTrunkAniPool fail!", e);
		}
		return responseUtil;
	}
	
	@POST
	@Path("check")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil checkTrunkName(CheckRequest number) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = trunkNumberDao.checkPoolName(number.getName(), number.getDomain());
			responseUtil = setResponseUtil(1, ret == true ? "true" : "false", null);
		} catch (Exception e) {
			Util.error(this, "check trunk name fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}

}
