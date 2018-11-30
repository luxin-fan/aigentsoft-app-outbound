package com.outbound.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.google.gson.Gson;
import com.outbound.common.BaseResource;
import com.outbound.common.PageRequest;
import com.outbound.common.Util;
import com.outbound.dialer.util.EncryptRosterUtil;
import com.outbound.impl.TaskContainer;
import com.outbound.impl.activity.RosterThread;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.impl.plan.PlanRosterThread;
import com.outbound.impl.util.AOUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.DbColumn;
import com.outbound.object.Roster;
import com.outbound.object.RosterBatch;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.RosterInfoListRequest;
import com.outbound.object.RosterTemplateInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.RosterBatchDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterDAO;
import com.outbound.object.dao.RosterTemplateDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/roster")
public class RosterResource extends BaseResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private Logger logger = Logger.getLogger(RosterResource.class);
	
	private RosterDAO rosterDao = (RosterDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterDAO");

	private RosterTemplateDAO rosterTemplateDao = (RosterTemplateDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterTemplateDAO");

	private RosterBatchDAO rosterBatchDao = (RosterBatchDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchDAO");

	private RosterBatchInfoDAO rosterBatchInfoDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");

	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getRosters(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}

			RosterTemplateInfo info = rosterTemplateDao.findByName(request.getDomain(), request.getTemplateName());
			if (info == null) {
				return gson.toJson(setResponseUtil(0, "模板不存在", null));
			}
			List<DbColumn> list = gson.fromJson(info.getColumns(), MetricUtil.typeDBCol);
			Map<String, String> tableHeadShow = new LinkedHashMap<String, String>();
			for (DbColumn dbColumn : list) {
				tableHeadShow.put(dbColumn.getName(), dbColumn.getShowName());
			}
			tableHeadShow.put("status", "拨打状态");

			List<Roster> lists = rosterDao.getTRosterInfos(request.getDomain(), request, startpage,
					request.getPageNum());

			for (Roster rosterInfo : lists) {
				EncryptRosterUtil.encryptRoster(rosterInfo, list);
			}

			int count = rosterDao.getTRosterInfoNum(request.getDomain(), request);
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("header", tableHeadShow);
			resultMap.put("list", lists == null ? new ArrayList<>() : lists);
			resultMap.put("count", count);
			responseUtil = setResponseUtil(1, "getRoster Suc", resultMap);
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getRosters fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("add")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public ResponseUtil addRoster(Roster template)
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{
			boolean ret = rosterDao.createRoster(template);
			if (ret == true)
			{
				responseUtil = setResponseUtil(1, "add Roster Suc", null);
			} else
			{
				responseUtil = setResponseUtil(0, "add Roster fail", null);
			}
		} catch (Exception e)
		{
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createRoster fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public ResponseUtil updateRoster(Roster template)
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{
			boolean ret = rosterDao.updateRoster(template);
			if (ret == true)
			{
				responseUtil = setResponseUtil(1, "update Roster Suc", null);
			} else
			{
				responseUtil = setResponseUtil(0, "update Roster fail", null);
			}
		} catch (Exception e)
		{
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateRoster fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteRoster(Roster template)
	{
		ResponseUtil responseUtil = new ResponseUtil();
		try
		{
			boolean ret = rosterDao.deleteRoster(template);
			if (ret == true)
			{
				responseUtil = setResponseUtil(1, "delete Roster Suc", null);
			} else
			{
				responseUtil = setResponseUtil(0, "delete Roster fail", null);
			}
		} catch (Exception e)
		{
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteRoster fail!", e);
		}
		return responseUtil;
	}

	/**
	 * @author zhangzj
	 * @date 2018/10/17
	 */
	@POST
	@Path("deleteRosterList")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteRosterList(RosterInfoListRequest rosterInfoListRequest) {
		ResponseUtil responseUtil = new ResponseUtil();
		RosterBatchInfo rosterBatchInfo = rosterBatchInfoDao.findByBatchName(rosterInfoListRequest.getDomain(),
				rosterInfoListRequest.getBatchName());
		RosterBatch rosterBatch = rosterBatchDao.findByBatchName(rosterInfoListRequest.getDomain(),
				rosterInfoListRequest.getBatchName());
		if (rosterInfoListRequest.getType() == 2) {
			try {
				rosterDao.clear(rosterInfoListRequest.getBatchName(), rosterInfoListRequest.getDomain());
				if (rosterBatchInfo != null) {

					rosterBatchInfo.setRoterNum(0);
					//这样写只能暂停的时候只有rosterbatch而没有rosterbatchinfo
					//rosterBatchInfo.setStatus(2);
					rosterBatchInfoDao.updateRosterBatchInfo(rosterBatchInfo);
				}
				rosterBatch.setRoterNum(0);
				//rosterBatch.setStatus(3);
				rosterBatchDao.updateRosterBatch(rosterBatch);
				return setResponseUtil(1, "成功", null);
			} catch (Exception e) {
				Util.error("", "删除失败，数据库操作失败" + e.getMessage());
				return setResponseUtil(0, "清空列表失败", null);
			}
		}
		String idss = rosterInfoListRequest.getRosterIdList().get(0);
		List<String> rosterIdList = new ArrayList<String>();
		for (int i = 0; i < idss.split(",").length; i++) {
			rosterIdList.add(idss.split(",")[i]);
		}
		List<Roster> rosterList = new ArrayList<Roster>();
		try {
			if (CollectionUtils.isEmpty(rosterIdList)) {
				throw new IllegalArgumentException("名单id不存在");
			}

			for (String id : rosterIdList) {
				Roster rosterInfo = rosterDao.findById(id);
				if (rosterInfo != null) {
					rosterList.add(rosterInfo);
				} else {
					throw new IllegalArgumentException("名单不存在");
				}
			}


			if (rosterBatch == null) {
				return setResponseUtil(0, "batch not exists", null);
			}

			if (rosterBatch.getStatus() != 0) {
				ActivityInfo activityInfo = activityDao.finActivityInfoByActivityName(rosterBatch.getActivityName(),
						rosterBatch.getDomain());
				if (activityInfo != null) {
					if (activityInfo.getActivityExecuteType() == 0) {
						RosterThread rosterThread = TaskContainer.findRosterThread(
								AOUtil.genernateRosterThdId(rosterBatch.getDomain(), rosterBatch.getActivityName()));
						if (rosterThread == null) {
							Util.error(RosterResource.class.getName(),
									"### roster not find roster thread:" + rosterBatch.getActivityName());
						} else {
							rosterThread.checkAndDelRoster(rosterBatch, rosterList, "clearRoster");
						}
					}
					if (activityInfo.getActivityExecuteType() == 1) {
						PlanRosterThread planRosterThread = TaskContainer.findPlanRosterThread(
								AOUtil.genernateRosterThdId(rosterBatch.getDomain(), rosterBatch.getActivityName()));
						if (planRosterThread == null) {
							Util.error(RosterResource.class.getName(),
									"### roster not find roster Planthread:" + rosterBatch.getActivityName());
						} else {
							planRosterThread.checkAndDelRoster(rosterBatch, rosterList, "clearRoster");
						}
					}
				}
			}
			boolean ret = false;
			for (String id : rosterInfoListRequest.getRosterIdList()) {
				String[] ids = id.split(",");
				for (String dix : ids) {
					Roster rosterInfo = rosterDao.findById(dix);
					rosterInfo.setStatus(0);
					ret = rosterDao.deleteRoster(rosterInfo);
				}
			}
			if (ret) {
				PageRequest pageRequest = new PageRequest();
				pageRequest.setDomain(rosterInfoListRequest.getDomain());
				pageRequest.setBatchName(rosterInfoListRequest.getBatchName());
				int afterbatchRosterCount = rosterDao.getTRosterInfoNum(rosterInfoListRequest.getDomain(), pageRequest);
				// 更改rosterbatchinfo中的num
				if (rosterBatchInfo != null) {
					rosterBatchInfo.setRoterNum(afterbatchRosterCount);
					//rosterBatchInfo.setStatus(2);
					rosterBatchInfoDao.updateRosterBatchInfo(rosterBatchInfo);
				}
				// 更改rosterbatch中的num
				rosterBatch.setRoterNum(afterbatchRosterCount);
				/*if(afterbatchRosterCount == 0) {
					rosterBatch.setStatus(3);
				}*/
				logger.info(rosterBatch.getStatus());
				rosterBatchDao.updateRosterBatch(rosterBatch);
				return setResponseUtil(1, "成功", null);
			} else {
				return setResponseUtil(0, "失败", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, "执行有误", null);
			Util.error(this, "deleteRoster fail!", e);
		}
		return responseUtil;
	}

	/**
	 * 禁呼
	 * 
	 * @author fanlx
	 * @date 2018/10/26
	 */
	@POST
	@Path("addBanRoster")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addBanRoster(RosterInfoListRequest rosterInfoListRequest) {
		ResponseUtil responseUtil = new ResponseUtil();
		List<String> rosterIdList = rosterInfoListRequest.getRosterIdList();
		List<Roster> rosterList = new ArrayList<Roster>();
		try {
			if (CollectionUtils.isEmpty(rosterIdList)) {
				throw new IllegalArgumentException("名单id不存在");
			}

			for (String id : rosterIdList) {
				Roster rosterInfo = rosterDao.findById(id);
				if (rosterInfo.getStatus() == 3) {
					throw new IllegalArgumentException("当前名单已禁呼");
				}
				if (rosterInfo.getStatus() == 2) {
					throw new IllegalArgumentException("当前名单已完成，无法设置禁呼");
				}
				rosterInfo.setStatus(3);

				rosterList.add(rosterInfo);
			}
			RosterBatch rosterBatch = rosterBatchDao.findByBatchName(rosterInfoListRequest.getDomain(),
					rosterInfoListRequest.getBatchName());
			// 查看批次是否提取
			if (rosterBatch == null) {
				throw new IllegalArgumentException("批次不存在");
			}

			if (rosterBatch.getStatus() != 3) {
				ActivityInfo activityInfo = activityDao.finActivityInfoByActivityName(rosterBatch.getActivityName(),
						rosterBatch.getDomain());
				if (activityInfo != null) {
					if (activityInfo.getActivityExecuteType() == 0) {
						RosterThread rosterThread = TaskContainer.findRosterThread(
								AOUtil.genernateRosterThdId(rosterBatch.getDomain(), rosterBatch.getActivityName()));
						if (rosterThread == null) {
							Util.error(RosterResource.class.getName(),
									"### roster not find roster thread:" + rosterBatch.getActivityName());
						} else {
							rosterThread.checkAndDelRoster(rosterBatch, rosterList, "");
						}
					}
					if (activityInfo.getActivityExecuteType() == 1) {
						PlanRosterThread planRosterThread = TaskContainer.findPlanRosterThread(
								AOUtil.genernateRosterThdId(rosterBatch.getDomain(), rosterBatch.getActivityName()));
						if (planRosterThread == null) {
							Util.error(RosterResource.class.getName(),
									"### roster not find roster Planthread:" + rosterBatch.getActivityName());
						} else {
							planRosterThread.checkAndDelRoster(rosterBatch, rosterList, "");
						}
					}
				}
			} else {
				throw new IllegalArgumentException("当前批次已完成，无法设置禁呼");
			}

			// 更新库信息
			boolean ret = false;
			for (Roster r : rosterList) {
				// 禁呼相当于一个重置操作
				ret = rosterDao.updateRoster(r);
			}
			if (ret) {
				responseUtil = setResponseUtil(1, "addBanRoster Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "addBanRoster fail", null);
			}
		} catch (IllegalArgumentException e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, "新增禁呼异常", null);
			Util.error(this, "addBanRoster fail!", e);
		}
		return responseUtil;
	}

	/**
	 * 取消禁呼
	 * 
	 * @author fanlx
	 * @date 2018/10/26
	 */
	@POST
	@Path("deleteBanRoster")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteBanRoster(RosterInfoListRequest rosterInfoListRequest) {
		ResponseUtil responseUtil = new ResponseUtil();
		List<Roster> banRosterList = new ArrayList<Roster>();
		try {
			if (CollectionUtils.isEmpty(rosterInfoListRequest.getRosterIdList())) {
				throw new IllegalArgumentException("名单id不存在");
			}
			// 查看批次是否提取
			RosterBatch rosterBatch = rosterBatchDao.findByBatchName(rosterInfoListRequest.getDomain(),
					rosterInfoListRequest.getBatchName());

			if (rosterBatch == null) {
				throw new IllegalArgumentException("未找到进行中批次");
			}

			if (rosterBatch.getStatus() == 3) {
				throw new IllegalArgumentException("当前批次已完成，不可取消禁呼");
			}

			boolean ret = false;
			for (String id : rosterInfoListRequest.getRosterIdList()) {
				Roster rosterInfo = rosterDao.findById(id);
				if (rosterInfo.getStatus() == 2) {
					throw new IllegalArgumentException("当前名单已完成，无法设置取消禁呼");
				}
				if (rosterInfo.getStatus() == 3) {
					rosterInfo.setStatus(0);
					ret = rosterDao.updateRoster(rosterInfo);
					banRosterList.add(rosterInfo);
				} else {
					throw new IllegalArgumentException("当前名单已为未禁呼状态");
				}
			}
			ActivityInfo activityInfo = activityDao.finActivityInfoByActivityName(rosterBatch.getActivityName(),
					rosterBatch.getDomain());
			if (activityInfo != null) {
				if (activityInfo.getActivityExecuteType() == 0) {
					RosterThread rosterThread = TaskContainer.findRosterThread(
							AOUtil.genernateRosterThdId(rosterBatch.getDomain(), rosterBatch.getActivityName()));
					if (rosterThread == null) {
						Util.error(RosterResource.class.getName(),
								"### roster not find roster thread:" + rosterBatch.getActivityName());
					} else {
						rosterThread.delBanRoster(rosterBatch, banRosterList);
					}
				}
				if (activityInfo.getActivityExecuteType() == 1) {
					PlanRosterThread planRosterThread = TaskContainer.findPlanRosterThread(
							AOUtil.genernateRosterThdId(rosterBatch.getDomain(), rosterBatch.getActivityName()));
					if (planRosterThread == null) {
						Util.error(RosterResource.class.getName(),
								"### roster not find roster Planthread:" + rosterBatch.getActivityName());
					} else {
						planRosterThread.delBanRoster(rosterBatch, banRosterList);
					}
				}
			}
			if (ret) {
				responseUtil = setResponseUtil(1, "deleteBanRoster Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "deleteBanRoster fail", null);
			}
		} catch (IllegalArgumentException e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteBanRoster fail!", e);
		}
		return responseUtil;
	}
}
