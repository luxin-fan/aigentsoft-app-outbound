package com.outbound.resources;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
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
import com.outbound.common.CheckRequest;
import com.outbound.common.PageRequest;
import com.outbound.common.Util;
import com.outbound.dialer.util.POIUtil;
import com.outbound.dialer.util.XlsUtil;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.job.CronJobManager;
import com.outbound.job.RosterDBImportJob;
import com.outbound.job.RosterImportJob;
import com.outbound.object.ActivityInfo;
import com.outbound.object.AutoImportConfigModel;
import com.outbound.object.DbColumn;
import com.outbound.object.FilterConditionModel;
import com.outbound.object.ImportResult;
import com.outbound.object.Roster;
import com.outbound.object.RosterBatch;
import com.outbound.object.RosterTemplateInfo;
import com.outbound.object.RosterTemplatePreparedField;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.ActivityInfoHistoryDAO;
import com.outbound.object.dao.DNCNumberDAO;
import com.outbound.object.dao.RosterBatchDAO;
import com.outbound.object.dao.RosterDAO;
import com.outbound.object.dao.RosterTemplateDAO;
import com.outbound.object.dao.RosterTemplatePreFieldDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.GsonFactory;
import com.outbound.object.util.ResponseUtil;
import com.outbound.object.util.TimeUtil;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

@Path("/rosterTemplate")
public class RosterTemplateResource extends BaseResource
{

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private RosterTemplateDAO rosterTemplateDao = (RosterTemplateDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterTemplateDAO");

	private RosterDAO rosterDao = (RosterDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterDAO");

	private RosterBatchDAO rosterBatchDao = (RosterBatchDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchDAO");

	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	private RosterTemplatePreFieldDAO rosterPreFieldDao = (RosterTemplatePreFieldDAO) ApplicationContextUtil
			.getApplicationContext().getBean("RosterTemplatePreFieldDAO");

	private DNCNumberDAO dncDao = (DNCNumberDAO) ApplicationContextUtil.getApplicationContext().getBean("DNCNumberDAO");

	private ActivityInfoHistoryDAO activityHisDao = (ActivityInfoHistoryDAO) ApplicationContextUtil
			.getApplicationContext().getBean("ActivityInfoHistoryDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public String getRosterTemplates(PageRequest request)
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{
			int startpage = request.getStartPage();
			if (startpage > 0)
			{
				startpage--;
			}
			List<RosterTemplateInfo> lists = rosterTemplateDao.getTRosterTemplateInfos(request.getDomain(),
					request.getTemplateName(), startpage, request.getPageNum());
			for (RosterTemplateInfo tInfo : lists)
			{
				List<DbColumn> list = gson.fromJson(tInfo.getColumns(), MetricUtil.typeDBCol);
				tInfo.setDbcolumns(list);
				int contactNum = rosterDao.getContactNums(request.getDomain(), tInfo.getName());
				tInfo.setContactNums(contactNum);
			}
			int count = rosterTemplateDao.getTRosterTemplateInfoNum(request.getDomain(), request.getTemplateName());
			responseUtil = setResponseUtil(1, "getRosterTemplate Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e)
		{
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getRosterTemplates fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("listActivityRT")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public String listActivityRT(PageRequest request)
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{
			int startpage = request.getStartPage();
			if (startpage > 0)
			{
				startpage--;
			}

			List<RosterTemplateInfo> listRosterTemplateInfo = new ArrayList<>();
			List<RosterTemplateInfo> lists = rosterTemplateDao.getTRosterTemplateInfos(request.getDomain(), "",
					startpage, request.getPageNum());
			lists = lists == null ? new ArrayList<RosterTemplateInfo>() : lists;
			for (RosterTemplateInfo rosterTemplateInfo : lists)
			{
				if (activityDao.getTActivityInfos(request.getDomain(), rosterTemplateInfo.getName()) != null
						|| activityHisDao.getTActivityInfosHis(request.getDomain(),
								rosterTemplateInfo.getName()) != null)
				{
					continue;
				}
				listRosterTemplateInfo.add(rosterTemplateInfo);

			}
			responseUtil = setResponseUtil(1, "listActivityRT Suc",
					super.getMergeSumAndList(listRosterTemplateInfo, listRosterTemplateInfo.size()));
		} catch (Exception e)
		{
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "listActivityRT fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("listPreField")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public String listPreField()
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{
			List<RosterTemplatePreparedField> lists = rosterPreFieldDao.getTRosterTemplatePreparedFields();
			int count = 0;
			if (lists != null && lists.size() > 0)
			{
				count = lists.size();
			}
			responseUtil = setResponseUtil(1, "getPreField Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e)
		{
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getRosterTemplates fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public ResponseUtil addRosterTemplate(RosterTemplateInfo template)
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{
			template.setColumns(gson.toJson(template.getDbcolumns()));
			template.setCreatetime(TimeUtil.getCurrentTimeStr());
			template.setLastModifyTime(TimeUtil.getCurrentTimeStr());
			boolean ret = rosterTemplateDao.createRosterTemplateInfo(template);
			if (ret == true)
			{
				responseUtil = setResponseUtil(1, "add RosterTemplate Suc", null);
			} else
			{
				responseUtil = setResponseUtil(0, "add RosterTemplate fail", null);
			}
		} catch (Exception e)
		{
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createRosterTemplate fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public ResponseUtil updateRosterTemplate(RosterTemplateInfo template)
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{
			if (template.getId() == 0)
			{
				RosterTemplateInfo t_template = rosterTemplateDao.findByName(template.getDomain(), template.getName());
				if (t_template != null)
				{
					t_template.setImportMode(template.getImportMode());
					t_template.setImportPath(template.getImportPath());
					t_template.setImportTime(TimeUtil.getCurrentTimeStr());
					t_template.setFilterCondition(template.getFilterCondition());
					t_template.setExecuteTime(template.getExecuteTime());
					t_template.setLastImportTime(t_template.getImportTime());

					if (t_template.getImportMode().equals("2"))
					{
						AutoImportConfigModel config = GsonFactory.getGson().fromJson(t_template.getImportPath(),
								AutoImportConfigModel.class);
						if (config != null)
						{
							CronJobManager.removeJob(t_template.getId() + "_roster_job");
							if (config.getSource().equals("file"))
							{
								CronJobManager.addJob(t_template.getId() + "_roster_job", RosterImportJob.class,
										t_template.getExecuteTime());
							} else if (config.getSource().equals("db"))
							{
								CronJobManager.addJob(t_template.getId() + "_roster_job", RosterDBImportJob.class,
										t_template.getExecuteTime());
							}
						}
					}

					boolean ret = rosterTemplateDao.updateRosterTemplateInfo(t_template);
					if (ret == true)
					{
						responseUtil = setResponseUtil(1, "update Roster Template Suc", null);
					} else
					{
						responseUtil = setResponseUtil(0, "update Roster Template fail", null);
					}
				}
			} else
			{
				/**
				 * @author zzj
				 * 修改模板时时间改变
				 */
				template.setLastModifyTime(TimeUtil.getCurrentTimeStr());
				boolean ret = rosterTemplateDao.updateRosterTemplateInfo(template);
				if (template.getImportMode().equals("2"))
				{
					AutoImportConfigModel config = GsonFactory.getGson().fromJson(template.getImportPath(),
							AutoImportConfigModel.class);
					if (config != null)
					{
						CronJobManager.removeJob(template.getId() + "_roster_job");
						if (config.getSource().equals("file"))
						{
							CronJobManager.addJob(template.getId() + "_roster_job", RosterImportJob.class,
									template.getExecuteTime());
						}
					}
				}
				if (ret == true)
				{
					responseUtil = setResponseUtil(1, "update Roster Template Suc", null);
				} else
				{
					responseUtil = setResponseUtil(0, "update Roster Template fail", null);
				}
			}

		} catch (Exception e)
		{
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateRosterTemplate fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteRosterTemplate(RosterTemplateInfo template)
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{

			ActivityInfo info = activityDao.findActivityInfoByTemplate(template.getName());
			if (info != null)
			{
				responseUtil = setResponseUtil(0, "模板已关联活动，不能删除", null);
				return responseUtil;
			}
			boolean ret = rosterTemplateDao.deleteRosterTemplateInfo(template);
			if (ret == true)
			{
				responseUtil = setResponseUtil(1, "delete Roster Template Suc", null);
			} else
			{
				responseUtil = setResponseUtil(0, "delete Roster Template fail", null);
			}
		} catch (Exception e)
		{
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteRosterTemplate fail!", e);
		}
		return responseUtil;
	}

	@GET
	@Path("download/{domain}/{templateName}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void downloadTemplate(@PathParam("domain") String domain, @PathParam("templateName") String templateName,
			@Context HttpServletRequest request, @Context HttpServletResponse response)
	{
		try
		{
			RosterTemplateInfo roster = rosterTemplateDao.findByName(domain, templateName);
			if (roster == null)
				return;
			String cols = roster.getColumns();
			List<DbColumn> list = gson.fromJson(cols, MetricUtil.typeDBCol);
			List<String> headers = new ArrayList<String>();
			for (DbColumn column : list)
			{
				headers.add(column.getName());
			}
			XlsUtil.exportRosterXls(response, headers, templateName);
			return;
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

	@POST
	@Path("check")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil checkTrunkName(CheckRequest number)
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{
			boolean ret = rosterTemplateDao.checkName(number.getName(), number.getDomain());
			responseUtil = setResponseUtil(1, ret == true ? "true" : "false", null);
		} catch (Exception e)
		{
			Util.error(this, "check trunk name fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}

	@POST
	@Path("import")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public ImportResult importRoster(FormDataMultiPart form, @Context HttpServletResponse response)
			throws UnsupportedEncodingException
	{
		ImportResult res = new ImportResult();
		try
		{
			response.setCharacterEncoding("UTF-8");
			// 获取文件流
			FormDataBodyPart filePart = form.getField("file");
			if (filePart == null)
			{
				throw new IllegalArgumentException("请选择要上传的文件");
			}
			FormDataBodyPart rosterNamePart = form.getField("templateName");
			if (rosterNamePart == null)
			{
				throw new IllegalArgumentException("请输入名单模板名");
			}
			// FormDataBodyPart createFlagPart =
			// form.getField("isCreateRoster");
			FormDataBodyPart rosterBatchIdPart = form.getField("batchId");
			FormDataBodyPart domainPart = form.getField("domain");

			RosterTemplateInfo rosterTemplate = rosterTemplateDao.findByName(domainPart.getValue(),
					rosterNamePart.getValue());
			FilterConditionModel filterConditionModel = GsonFactory.getGson()
					.fromJson(rosterTemplate.getFilterCondition(), FilterConditionModel.class);
			if (filterConditionModel.getClear() == 1)
			{
				rosterDao.clearTemplate(rosterTemplate.getName(), rosterTemplate.getDomain());
				rosterBatchDao.clearTemplate(rosterTemplate.getName(), rosterTemplate.getDomain());
			}

			ArrayList<HashMap<String, String>> rosters = new ArrayList<HashMap<String, String>>();
			List<String[]> params = POIUtil.readRosterExcel(form);
			if (params != null && params.size() > 0)
			{
				String[] headers = params.get(0);
				for (int i = 1; i < params.size(); i++)
				{
					HashMap<String, String> rosterInfos = new HashMap<String, String>();
					for (int j = 0; j < headers.length; j++)
					{
						if (params.get(i).length <= j)
						{
							rosterInfos.put(headers[j], "");
						} 
						else
						{
							rosterInfos.put(headers[j], params.get(i)[j]);
						}
					}
					rosters.add(rosterInfos);
				}
			}

			// filter roster
			String templateName = rosterNamePart.getValue();
			String domain = domainPart.getValue();
			int count = 0;
			ActivityInfo info = activityDao.getTActivityInfos(domain, templateName);;
			
			if (rosters != null && rosters.size() > 0)
			{
				for (HashMap<String, String> rosterMap : rosters)
				{
					Roster rosterInfo = new Roster();
					rosterInfo.setDomain(domain);
					if (info != null)
					{
						rosterInfo.setActivityName(info.getName());
					}
					rosterInfo.setTemplateName(templateName);
					rosterInfo.setBatchName(rosterBatchIdPart.getValue());
					boolean hit = false;
					if (filterConditionModel.getKey() != null)
					{
						for (String key : filterConditionModel.getKey())
						{
							if (rosterMap.containsKey(key))
							{
								// int length =
								// rosterMap.get(key).trim().length();
								if ((rosterMap.get(key) != null))
								{
									int length = rosterMap.get(key).trim().length();
									if (length == 0)
									{
										hit = true;
										continue;
									}
								}
								else
								{
									hit = true;
									continue;
								}
							} 
							else
							{
								hit = true;
								continue;
							}
						}
					}
					if (hit)
					{
						continue;
					}
					if (rosterMap.containsKey("batchId"))
					{

						rosterInfo.setBatchName(rosterMap.get("batchId"));
						rosterMap.remove("batchId");
					}

					if (rosterMap.containsKey("phoneNum1"))
					{
						rosterInfo.setPhoneNum1(rosterMap.get("phoneNum1"));
						rosterMap.remove("phoneNum1");
					}
					if (rosterMap.containsKey("phoneNum2"))
					{
						rosterInfo.setPhoneNum2(rosterMap.get("phoneNum2"));
						rosterMap.remove("phoneNum2");
					}
					if (rosterMap.containsKey("phoneNum3"))
					{
						rosterInfo.setPhoneNum3(rosterMap.get("phoneNum3"));
						rosterMap.remove("phoneNum3");
					}
					if (rosterMap.containsKey("phoneNum4"))
					{
						rosterInfo.setPhoneNum4(rosterMap.get("phoneNum4"));
						rosterMap.remove("phoneNum4");
					}
					if (rosterMap.containsKey("phoneNum5"))
					{
						rosterInfo.setPhoneNum5(rosterMap.get("phoneNum5"));
						rosterMap.remove("phoneNum5");
					}
					
					if (filterConditionModel.getRemoval() == 1)
					{
						if (rosterDao.isRosteExit(rosterInfo))
						{
							continue;
						}
					}
					
					if (filterConditionModel.getDNC() != null && filterConditionModel.getDNC().size() > 0)
					{
						if (rosterInfo.getPhoneNum1() != null)
						{
							int ret = dncDao.getTDNCNumberNumQuery(rosterInfo.getDomain(),
									filterConditionModel.getDNC().get(0), rosterInfo.getPhoneNum1());
							if (ret > 0)
							{
								continue;
							}
						}

						if (rosterInfo.getPhoneNum2() != null)
						{
							int ret = dncDao.getTDNCNumberNumQuery(rosterInfo.getDomain(),
									filterConditionModel.getDNC().get(0), rosterInfo.getPhoneNum2());
							if (ret > 0)
							{
								continue;
							}
						}

						if (rosterInfo.getPhoneNum3() != null)
						{
							int ret = dncDao.getTDNCNumberNumQuery(rosterInfo.getDomain(),
									filterConditionModel.getDNC().get(0), rosterInfo.getPhoneNum3());
							if (ret > 0)
							{
								continue;
							}
						}
					}
					
					if (rosterMap.containsKey("lastname"))
					{
						rosterInfo.setLastname(rosterMap.get("lastname"));
						rosterMap.remove("lastname");
					}
					if (rosterMap.containsKey("firstname"))
					{
						rosterInfo.setFirstname(rosterMap.get("firstname"));
						rosterMap.remove("firstname");
					}
					if (rosterMap.containsKey("age"))
					{
						try
						{
							rosterInfo.setAge(Integer.parseInt(rosterMap.get("age")));
						} 
						catch (Exception e)
						{
							Util.warn("roster import", "age param is not integer !");
						}
						rosterMap.remove("age");
					}
					if (rosterMap.containsKey("sex"))
					{
						rosterInfo.setSex(rosterMap.get("sex"));
						rosterMap.remove("sex");
					}
					if (rosterMap.containsKey("customerId"))
					{
						rosterInfo.setCustomerId(rosterMap.get("customerId"));
						rosterMap.remove("customerId");
					}
					if (rosterMap.containsKey("address"))
					{
						rosterInfo.setAddress(rosterMap.get("address"));
						rosterMap.remove("address");
					}
					if (rosterMap.containsKey("email"))
					{
						rosterInfo.setEmail(rosterMap.get("email"));
						rosterMap.remove("email");
					}
					if (rosterMap.containsKey("cardType"))
					{
						rosterInfo.setCardType(rosterMap.get("cardType"));
						rosterMap.remove("cardType");
					}
					if (rosterMap.containsKey("cardNum"))
					{
						rosterInfo.setCardNum(rosterMap.get("cardNum"));
						rosterMap.remove("cardNum");
					}
					rosterInfo.setCreateTime(TimeUtil.getCurrentTimeStr());
					if (!rosterMap.isEmpty())
					{
						rosterInfo.setCustomFields(gson.toJson(rosterMap));
					}
					rosterInfo.setCallRound(1);
					
					rosterInfo.setReCall(0);
					rosterInfo.setMakeCallTime(TimeUtil.getCurrentTimeStr());
					

					rosterDao.createRoster(rosterInfo);
					count++;
				}

				RosterBatch rosterBatch = new RosterBatch();
				if (rosterBatchIdPart.getValue() != null)
				{
					String batchId = rosterBatchIdPart.getValue();
					rosterBatch.setBatchId(batchId);
					rosterBatch.setTemplateName(templateName);
					rosterBatch.setCreateTime(TimeUtil.getCurrentTimeStr());
					rosterBatch.setRoterNum(count);
					rosterBatch.setCallRound(1);
					rosterBatch.setStatus(0);
					rosterBatch.setDomain(domain);
					if (info != null)
					{
						rosterBatch.setActivityId(info.getId());
						rosterBatch.setActivityName(info.getName());
						info.addRosterNum(count);
						info.addBatchNum();
						activityDao.updateActivityInfo(info);
						MetricUtil.addRostersDay(info.getName(), domain, count);
					}
					rosterBatchDao.createRosterBatch(rosterBatch);
				}
			}
			// suc fail
			res = new ImportResult();
			res.setSucRows(count);
			res.setResult("suc");

		}
		catch (IllegalArgumentException e)
		{
			Util.error(this, "uploadManualRoster Fail!", e);
		} catch (Exception e)
		{
			Util.error(this, "uploadManualRoster Fail!", e);
		}
		return res;
	}
}
