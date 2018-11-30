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
import com.outbound.object.DNCNumber;
import com.outbound.object.DNCNumbers;
import com.outbound.object.DNCTemplate;
import com.outbound.object.FilterConditionModel;
import com.outbound.object.RecordId;
import com.outbound.object.RecordIds;
import com.outbound.object.dao.DNCNumberDAO;
import com.outbound.object.dao.DNCTemplateDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.GsonFactory;
import com.outbound.object.util.ResponseUtil;

@Path("/dncNumber")
public class DNCNumberResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private DNCNumberDAO dncDao = (DNCNumberDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("DNCNumberDAO");
	
	private DNCTemplateDAO dncTemplateDao = (DNCTemplateDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("DNCTemplateDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getDNCNumbers(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if(startpage > 0){
				startpage --;
			}
			List<DNCNumber> lists;
			int count;
			if(request.getPhoneNum()!= null&&request.getPhoneNum().length()>0){
				lists = dncDao.getTDNCNumbersQuery
						(request.getDomain(),request.getDncTemplateId(), request.getPhoneNum(),
								startpage,request.getPageNum());
				count = dncDao.getTDNCNumberNumQuery(request.getDomain(), request.getDncTemplateId(),
						request.getPhoneNum());
			}else{
				lists = dncDao.getTDNCNumbers
						(request.getDomain(),request.getDncTemplateId(), startpage,request.getPageNum());
				count = dncDao.getTDNCNumberNum(request.getDomain(), request.getDncTemplateId());
			}
			responseUtil = setResponseUtil(1, "getDNCNumber Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getDNCNumbers fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("clear")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil clearDNCNumber(RecordId rId) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = dncDao.clear(rId.getId()+"", rId.getDomain());
			if (ret == true) {
				responseUtil = setResponseUtil(1, "clear DncNumber Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "clear DncNumber fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateDNCNumber fail!", e);
		}
		return responseUtil;
	}
	
	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addDNCNumber(DNCNumber template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = dncDao.createDNCNumber(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "add DNCNumber Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "add DNCNumber fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createDNCNumber fail!", e);
		}
		return responseUtil;
	}
	
	@POST
	@Path("addList")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addDNCNumbers(DNCNumbers template) {
		
		ResponseUtil responseUtil = new ResponseUtil();
		if(template.getDncTemplateId() != null){
			DNCTemplate dncTemplate = dncTemplateDao.findById(template.getDncTemplateId());
			FilterConditionModel filterConditionModel = GsonFactory.getGson().fromJson(dncTemplate.getFilterCondition(), FilterConditionModel.class);
			if(filterConditionModel.getClear() == 1){
				dncDao.clear(dncTemplate.getId()+"", dncTemplate.getDomain());
			}
			try {
				for(String dncnumber:template.getPhoneNumList()){
					DNCNumber number = new DNCNumber();
					number.setDomain(template.getDomain());
					number.setDncTemplateId(""+template.getDncTemplateId());
					number.setPhoneNum(dncnumber);
					if(filterConditionModel.getRemoval() == 1){
						int count = dncDao.getTDNCNumberNumQuery(template.getDomain(), ""+template.getDncTemplateId(), dncnumber);
						if(count > 0){
							continue;
						}
					}
					boolean ret = dncDao.createDNCNumber(number);
					if (ret == true) {
						responseUtil = setResponseUtil(1, "add DNCNumber Suc", null);
					} else {
						responseUtil = setResponseUtil(0, "add DNCNumber fail", null);
						break;
					}
				}
				responseUtil = setResponseUtil(1, "add DNCNumber Suc", null);
			} catch (Exception e) {
				responseUtil = setResponseUtil(0, e.getMessage(), null);
				Util.error(this, "createDNCNumber fail!", e);
			}
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil updateDNCNumber(DNCNumber template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = dncDao.updateDNCNumber(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "update DncNumber Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "update DncNumber fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateDNCNumber fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteDNCNumber(DNCNumber template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = dncDao.deleteDNCNumber(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "delete DncNumber Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete DncNumber fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteDNCNumber fail!", e);
		}
		return responseUtil;
	}
	
	@POST
	@Path("deleteList")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteDNCNumber(RecordIds ids) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			for(String id : ids.getIds()){
				DNCNumber number =	dncDao.findById(id);
				boolean ret = dncDao.deleteDNCNumber(number);
				if (ret == true) {
					responseUtil = setResponseUtil(1, "delete DncNumber Suc", null);
				} else {
					responseUtil = setResponseUtil(0, "delete DncNumber fail", null);
					break;
				}
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteDNCNumber fail!", e);
		}
		return responseUtil;
	}
}
