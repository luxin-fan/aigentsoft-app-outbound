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
import com.outbound.object.Holiday;
import com.outbound.object.dao.HolidayDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/holiday")
public class HolidayResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private HolidayDAO holidayDao = (HolidayDAO) ApplicationContextUtil.getApplicationContext().getBean("HolidayDAO");
	static Gson gson = new Gson();
	
	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getHolidays(PageRequest request) {

		int spage = request.getStartPage();
		if(spage > 0){
			spage--;
		}
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			List<Holiday> lists = holidayDao.getTHolidays(request.getDomain(), spage,
					request.getPageNum());
			int count = holidayDao.getTHolidayNum(request.getDomain());
			responseUtil = setResponseUtil(1, "getHoliday Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getHolidays fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil addHoliday(Holiday holiday) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			if (holiday.getStartDate() == null)
				throw new IllegalArgumentException("请输入开始时间");
			holidayDao.createHoliday(holiday);
			responseUtil = setResponseUtil(1, "addHoliday Suc", null);
		} catch (Exception e) {
			Util.error(this, "addHoliday fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil deleteHoliday(Holiday holiday) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			holidayDao.deleteHoliday(holiday);
			responseUtil = setResponseUtil(1, "deleteHoliday Suc", null);
		} catch (Exception e) {
			Util.error(this, "deleteHoliday fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil updateHoliday(Holiday holiday) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			holidayDao.updateHoliday(holiday);
			responseUtil = setResponseUtil(1, "updateHoliday Suc", null);
		} catch (Exception e) {
			Util.error(this, "updateHoliday fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}
}
