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
import com.outbound.object.TrunkPoolCorr;
import com.outbound.object.dao.TrunkPoolCorrDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/trunkcorr")
public class TrunkPoolCorrResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private TrunkPoolCorrDAO TrunkPoolCorrDao = (TrunkPoolCorrDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("TrunkPoolCorrDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getTrunkPoolCorrs(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if(startpage > 0){
				startpage --;
			}
			List<TrunkPoolCorr> lists = TrunkPoolCorrDao.getTTrunkPoolCorrs(request.getDomain(), request.getPoolName(),
					startpage,
					request.getPageNum());
			int count = TrunkPoolCorrDao.getTTrunkPoolCorrNum(request.getDomain(),request.getPoolName());
			responseUtil = setResponseUtil(1, "getTrunkNumber Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getTrunkPoolCorrs fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addTrunkPool(TrunkPoolCorr trunkPool) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = TrunkPoolCorrDao.createTrunkPoolCorr(trunkPool);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "add TrunkPoolCorr Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "add TrunkPoolCorr fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createTrunkPoolCorr fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil updateTrunkPoolCorr(TrunkPoolCorr pool) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = TrunkPoolCorrDao.updateTrunkPoolCorr(pool);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "update Trunk Pool Corr Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "update Trunk Pool Corr fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateTrunkPoolCorr fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteTrunkPoolCorr(TrunkPoolCorr pool) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = TrunkPoolCorrDao.deleteTrunkPoolCorr(pool);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "delete Trunk Pool Corr Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete Trunk Pool Corr fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteTrunkPoolCorr fail!", e);
		}
		return responseUtil;
	}
}
