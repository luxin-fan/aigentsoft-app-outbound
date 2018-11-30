package com.outbound.resources;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.outbound.common.BaseResource;
import com.outbound.common.CheckRequest;
import com.outbound.common.PageRequest;
import com.outbound.common.Util;
import com.outbound.dialer.util.POIUtil;
import com.outbound.dialer.util.XlsUtil;
import com.outbound.job.CronJobManager;
import com.outbound.job.DNCImportJob;
import com.outbound.object.AutoImportConfigModel;
import com.outbound.object.DNCNumber;
import com.outbound.object.DNCTemplate;
import com.outbound.object.FilterConditionModel;
import com.outbound.object.dao.DNCNumberDAO;
import com.outbound.object.dao.DNCTemplateDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.GsonFactory;
import com.outbound.object.util.ResponseUtil;
import com.outbound.object.util.TimeUtil;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

@Path("/dncTemplate")
public class DNCTemplateResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private DNCTemplateDAO dncDao = (DNCTemplateDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("DNCTemplateDAO");
	private DNCNumberDAO dncNumDao = (DNCNumberDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("DNCNumberDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getDNCTemplates(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}
			if(request.getTemplateName()!= null && request.getTemplateName().trim().length()>0){
				List<DNCTemplate> lists = dncDao.getTDNCTemplates(request.getDomain(),request.getTemplateName().trim(),startpage, request.getPageNum());
				if(lists != null){
					for(DNCTemplate template : lists){
						int dncNum = dncNumDao.getTDNCNumberNum(template.getDomain(), ""+template.getId());
						template.setDncRosterNum(dncNum);
					}
				}
				int count = dncDao.getTDNCTemplateNum(request.getDomain(),request.getTemplateName().trim());
				responseUtil = setResponseUtil(1, "getDNCTemplate Suc",
						super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
			}else{
				List<DNCTemplate> lists = dncDao.getTDNCTemplates(request.getDomain(), startpage, request.getPageNum());
				if(lists != null){
					for(DNCTemplate template : lists){
						int dncNum = dncNumDao.getTDNCNumberNum(template.getDomain(), ""+template.getId());
						template.setDncRosterNum(dncNum);
					}
				}
				int count = dncDao.getTDNCTemplateNum(request.getDomain());
				responseUtil = setResponseUtil(1, "getDNCTemplate Suc",
						super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getDNCTemplates fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public String addDNCTemplate(DNCTemplate template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			template.setUpdateTime(TimeUtil.getCurrentTimeStr());
			boolean ret = dncDao.createDNCTemplate(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "add DNCTemplate Suc", template);
			} else {
				responseUtil = setResponseUtil(0, "add DNCTemplate fail", template);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), template);
			Util.error(this, "createDNCTemplate fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateDNCTemplate(DNCTemplate template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			if(template.getDncTemplateName() == null){
				DNCTemplate tem= dncDao.findById(""+template.getId());
				tem.setFilterCondition(template.getFilterCondition());
				tem.setServerType(template.getServerType());
				tem.setUpdateTime(TimeUtil.getCurrentTimeStr());
				boolean ret = dncDao.updateDNCTemplate(tem);
				if (ret == true) {
					responseUtil = setResponseUtil(1, "update DNCTemplate Suc", tem);
				} else {
					responseUtil = setResponseUtil(0, "update DNCTemplate fail", tem);
				}
			}else{
				template.setUpdateTime(TimeUtil.getCurrentTimeStr());
				boolean ret = dncDao.updateDNCTemplate(template);
				if(template.getImportMode()!= null && template.getImportMode().equals("2")){
					AutoImportConfigModel config = GsonFactory.getGson().fromJson(template.getImportPath(), AutoImportConfigModel.class);
					if(config!= null){
						CronJobManager.removeJob(template.getId() + "_dnc_job");
						if(config.getSource().equals("file")){
							CronJobManager.addJob(template.getId() + "_dnc_job", DNCImportJob.class, template.getExecuteTime() );
						}
					}
				}
				if (ret == true) {
					responseUtil = setResponseUtil(1, "update DNCTemplate Suc", template);
				} else {
					responseUtil = setResponseUtil(0, "update DNCTemplate fail", template);
				}
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateDNCTemplate fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteDNCTemplate(DNCTemplate template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = dncDao.deleteDNCTemplate(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "delete DNCTemplate Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete DNCTemplate fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteDNCTemplate fail!", e);
		}
		return responseUtil;
	}

	@GET
	@Path("download")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void downloadTemplate(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		try {
			XlsUtil.exportDNSXls(response);
		} catch (Exception e) {
			Util.error(this, "download template error", e);
		}
	}
	
	@POST
	@Path("check")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil checkTrunkName(CheckRequest number) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = dncDao.checkDNCName(number.getName(), number.getDomain());
			responseUtil = setResponseUtil(1, ret == true ? "true" : "false", null);
		} catch (Exception e) {
			Util.error(this, "check trunk name fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}

	
	@POST
	@Path("import")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil importDncRoster(FormDataMultiPart form, @Context HttpServletResponse response)
			throws UnsupportedEncodingException {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			// 获取文件流
			FormDataBodyPart filePart = form.getField("file");
			FormDataBodyPart domainPart = form.getField("domain");
			FormDataBodyPart dncTemplateIdPart = form.getField("id");
			
			if (filePart == null)
				throw new IllegalArgumentException("请输入上传文件");
			if (domainPart == null || StringUtils.isBlank(domainPart.getValue()))
				throw new IllegalArgumentException("请输入domain信息");
			if (dncTemplateIdPart == null || StringUtils.isBlank(dncTemplateIdPart.getValue()))
				throw new IllegalArgumentException("请输入Dnc模板Id");
			response.setCharacterEncoding("UTF-8");
			
			//dncDao
			DNCTemplate dncTemplate = dncDao.findById(dncTemplateIdPart.getValue());
			FilterConditionModel filterConditionModel = GsonFactory.getGson().fromJson(dncTemplate.getFilterCondition(), FilterConditionModel.class);
			if(filterConditionModel.getClear() == 1){
				dncNumDao.clear(dncTemplate.getId()+"", dncTemplate.getDomain());
			}
			dncTemplate.setUpdateTime(TimeUtil.getCurrentTimeStr());
			dncDao.updateDNCTemplate(dncTemplate);
			
			List<String[]> params = POIUtil.readExcel(form);
			if(params != null && params.size() >0 ){
				for(String[] paramLine : params){
					DNCNumber dncNum = new DNCNumber();
					dncNum.setPhoneNum(paramLine[0]);
					dncNum.setDncTemplateId(dncTemplateIdPart.getValue());
					dncNum.setDomain(domainPart.getValue());
					if(filterConditionModel.getRemoval() == 1){
						int count = dncNumDao.getTDNCNumberNumQuery(dncNum.getDomain(), ""+dncNum.getDncTemplateId(), dncNum.getPhoneNum());
						if(count > 0){
							continue;
						}
					}
					boolean result = dncNumDao.createDNCNumber(dncNum);
					if(!result){
						responseUtil = super.setResponseUtil(0, "导入dnc名单失败", null);
						break;
					}
				}
				responseUtil = super.setResponseUtil(1, "导入dnc名单成功", null);
			}else{
				responseUtil = super.setResponseUtil(0, "名单数据为空", null);
			}
			
		} catch (IllegalArgumentException e) {
			responseUtil = super.setResponseUtil(0, e.getMessage(), null);
		} catch (Exception e) {
			Util.error(this, "addDncRosterTemplate faill", e);
			responseUtil = super.setResponseUtil(1, "导入dnc名单成功", null);
		}
		return responseUtil;
	}
}
