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
import com.outbound.impl.TaskContainer;
import com.outbound.impl.activity.RosterThread;
import com.outbound.impl.plan.PlanRosterThread;
import com.outbound.impl.util.AOUtil;
import com.outbound.object.ActivityInfo;
import com.outbound.object.ChangeBatchInfo;
import com.outbound.object.RosterBatch;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.RosterBatchDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.dao.RosterDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;

@Path("/rosterBatch")
public class RosterBatchResource extends BaseResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private RosterBatchInfoDAO rosterBatchInfoDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");

	private RosterBatchDAO rosterBatchDao = (RosterBatchDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchDAO");
	
	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	private RosterDAO rosterDao = (RosterDAO) ApplicationContextUtil.getApplicationContext().getBean("RosterDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getRosterBatchs(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}
			List<RosterBatch> lists = rosterBatchDao.getTRosterBatchInfos(request.getDomain(),
					request.getTemplateName(), startpage, request.getPageNum());

			int count = rosterBatchInfoDao.getTRosterBatchInfoNum(request.getDomain(), request.getTemplateName());

			responseUtil = setResponseUtil(1, "getRosterBatch Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getRosterBatchs fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("listUncall")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getUncallRosterBatchs(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}
			List<RosterBatch> lists = rosterBatchDao.getUnCallRosterBatchInfos(request.getDomain(),
					request.getTemplateName(), request.getBatchName(), startpage, request.getPageNum());
			int count = rosterBatchDao.getUncallRosterBatchInfoNum(request.getDomain(), request.getTemplateName());
			responseUtil = setResponseUtil(1, "getRosterBatch Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getRosterBatchs fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("check")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil checkBatchName(CheckRequest number) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = rosterBatchDao.checkName(number.getName(), number.getDomain());
			responseUtil = setResponseUtil(1, ret == true ? "true" : "false", null);
		} catch (Exception e) {
			Util.error(this, "check trunk name fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil updateBatch(ChangeBatchInfo batchInfo) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int ret = rosterBatchDao.updateRosterBatchIdo(batchInfo);
			responseUtil = setResponseUtil(1, ret == 1 ? "true" : "false", null);
		} catch (Exception e) {
			Util.error(this, "check trunk name fail!", e);
			responseUtil = setResponseUtil(0, "该批次ID已存在", null);
		}
		return responseUtil;
	}

	/**
	 * @author fanlx
	 * @date 2018.10.16
	 */
	@POST
	@Path("delete")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil deleteBatch(RosterBatch rosterBatch) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			/**
			 * 刚导入批次已经拉取，多个存储的清除
			 */
			if (rosterBatch.getStatus() != 0) {
				//判断活动类型
				ActivityInfo activityInfo = activityDao.finActivityInfoByActivityName(rosterBatch.getActivityName(), rosterBatch.getDomain());
				if(activityInfo != null){
					if(activityInfo.getActivityExecuteType() == 0){
						RosterThread rosterThread = TaskContainer.findRosterThread(
								AOUtil.genernateRosterThdId(rosterBatch.getDomain(), rosterBatch.getActivityName()));
						if (rosterThread == null) {
							Util.error(RosterBatchResource.class.getName(),
									"### roster not find roster thread:" + rosterBatch.getActivityName());
						} else {
							rosterThread.checkAndDelRoster(rosterBatch, null, "deleteBatch");
						}
					}
					if(activityInfo.getActivityExecuteType() == 1){
						PlanRosterThread planRosterThread = TaskContainer.findPlanRosterThread(AOUtil.genernateRosterThdId(rosterBatch.getDomain(), rosterBatch.getActivityName()));
						if (planRosterThread == null) {
							Util.error(RosterBatchResource.class.getName(),
									"### roster not find roster Planthread:" + rosterBatch.getActivityName());
						} else {
							planRosterThread.checkAndDelRoster(rosterBatch, null, "deleteBatch");
						}
					}
				}
				// 先清空rosterPool中的内容
			}
			rosterDao.clear(rosterBatch.getBatchId(), rosterBatch.getDomain());
			boolean ret = rosterBatchDao.deleteRosterBatch(rosterBatch);
			rosterBatchInfoDao.deleteByRosterId(rosterBatch.getBatchId(), rosterBatch.getDomain());
			responseUtil = setResponseUtil(1, ret == true ? "true" : "false", null);
		} catch (Exception e) {
			Util.error(this, "删除批次失败", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}
}
