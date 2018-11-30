package com.outbound.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionException;

import com.google.gson.Gson;
import com.outbound.common.BaseResource;
import com.outbound.common.CheckRequest;
import com.outbound.common.PageRequest;
import com.outbound.common.Util;
import com.outbound.impl.TaskContainer;
import com.outbound.impl.activity.ActivityThread;
import com.outbound.impl.activity.RosterPool;
import com.outbound.impl.activity.RosterThread;
import com.outbound.impl.plan.PlanActivityThread;
import com.outbound.impl.plan.PlanRosterPool;
import com.outbound.impl.plan.PlanRosterThread;
import com.outbound.impl.util.AOUtil;
import com.outbound.impl.util.ReqResponse;
import com.outbound.job.ActivityCronExcuteJob;
import com.outbound.job.CronJobManager;
import com.outbound.job.DayClearJob;
import com.outbound.job.RosterExportJob;
import com.outbound.job.SimpleJobManager;
import com.outbound.job.StopTask;
import com.outbound.object.ActivityInfo;
import com.outbound.object.OutboundPolicyInfo;
import com.outbound.object.OutboundRecallPolicy;
import com.outbound.object.RecordId;
import com.outbound.object.RosterBatchInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.OutboundPolicyInfoDAO;
import com.outbound.object.dao.OutboundRecallPolicyDAO;
import com.outbound.object.dao.RosterBatchInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.ResponseUtil;
import com.outbound.object.util.TimeUtil;

@Path("/activity")
public class ActivityResource extends BaseResource {
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	private Logger logger = Logger.getLogger(ActivityResource.class);

	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	private OutboundPolicyInfoDAO outboundPolicyInfoDao = (OutboundPolicyInfoDAO) ApplicationContextUtil
			.getApplicationContext().getBean("OutboundPolicyInfoDAO");

	private RosterBatchInfoDAO rosterBatchDao = (RosterBatchInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterBatchInfoDAO");

	private OutboundRecallPolicyDAO recallPolicyDao = (OutboundRecallPolicyDAO) ApplicationContextUtil
			.getApplicationContext().getBean("OutboundRecallPolicyDAO");

	static Gson gson = new Gson();

	@POST
	@Path("list")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getActivityInfos(PageRequest request) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			int startpage = request.getStartPage();
			if (startpage > 0) {
				startpage--;
			}
			List<ActivityInfo> lists = activityDao.getTActivityInfos(request);
			int count = activityDao.getTActivityInfoNum(request);
			if (lists != null) {
				for (ActivityInfo info : lists) {
					List<OutboundRecallPolicy> policys = recallPolicyDao.getTOutboundRecallPolicys(info.getDomain(),
							info.getName(), 0, 10);
					info.setRoundList(policys);
				}
			}
			responseUtil = setResponseUtil(1, "getActivityInfo Suc",
					super.getMergeSumAndList(lists == null ? new ArrayList<>() : lists, count));
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "getActivityInfos fail!", e);
		}
		return gson.toJson(responseUtil);
	}

	@POST
	@Path("monitorlist")
	@Produces({ MediaType.APPLICATION_JSON })
	public ReqResponse montir(PageRequest request) {
		ReqResponse res = new ReqResponse();
		res.setCode(200);
		return res;
	}

	@GET
	@Path("genHis")
	@Produces({ MediaType.APPLICATION_JSON })
	public ReqResponse genHis() {

		DayClearJob job = new DayClearJob();
		try {
			job.execute(null);
		} catch (JobExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ReqResponse res = new ReqResponse();
		res.setCode(200);
		return res;
	}

	@POST
	@Path("add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil addActivityInfo(ActivityInfo template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			if (StringUtils.isNotBlank(template.getOrderType()) && template.getOrderType().length() > 40) {
				throw new IllegalArgumentException("排序字段不能超过40个字符");
			}
			// 查看活动是否关联名单
			if (activityDao.getTActivityInfos(template.getDomain(), template.getRosterTemplateName()) != null) {
				return responseUtil = setResponseUtil(0, "当前名单已经被其他活动占用，请重新选择名单", null);
			}
			List<RosterBatchInfo> num = rosterBatchDao.getUnCallRosterBatchInfos(template.getDomain(),
					template.getRosterTemplateName(), null, 0, 1000);
			if (num != null) {
				template.setBatchNum(num.size());
				for (RosterBatchInfo info : num) {
					template.addRosterNum(info.getRoterNum());
					info.setActivityName(template.getName());
					rosterBatchDao.updateRosterBatchInfo(info);
				}
			}
			boolean ret = activityDao.createActivityInfo(template);

			List<OutboundRecallPolicy> rounds = template.getRoundList();
			recallPolicyDao.deleteOutboundRecallPolicy(template.getDomain(), template.getName());
			for (OutboundRecallPolicy round : rounds) {
				round.setDomain(template.getDomain());
				round.setActivityName(template.getName());
				recallPolicyDao.createOutboundRecallPolicy(round);
			}

			if (ret == true) {
				responseUtil = setResponseUtil(1, "add ActivityInfo Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "add ActivityInfo fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "createActivityInfo fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil updateActivityInfo(ActivityInfo template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			ActivityInfo aInfo = activityDao.findById("" + template.getId());
			if (aInfo != null) {
				template.setStatus(aInfo.getStatus());
			}
			boolean ret = activityDao.updateActivityInfo(template);
			if (ret == true) {
				responseUtil = setResponseUtil(1, "update Activity Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "update Activity fail", null);
			}

			if (template.getActivityExecuteType() == 0) {
				SimpleJobManager.removeJob(AOUtil.genernateActivityThdId(template.getDomain(), template.getName()));
				long now = System.currentTimeMillis();
				long finshTime = TimeUtil.getDateTime(template.getEndDatetime());
				int timeOut = Integer.parseInt((finshTime - now) / 1000 + "");
				logger.info((finshTime - now) + "  ms=====>s  " + timeOut);
				if (timeOut > 0) {
					SimpleJobManager.addJob(AOUtil.genernateActivityThdId(template.getDomain(), template.getName()),
							StopTask.class, timeOut, 0, 0);
				}
			}
			if (template.getActivityExecuteType() == 1) {
				String activityName = template.getName() + ":" + template.getDomain();
				SimpleJobManager.removeJob(activityName);
				String excuteTime = template.getActivityExecuteTime();
				if (excuteTime.contains("/")) {
					excuteTime = excuteTime.substring(3, excuteTime.length());
					excuteTime = excuteTime.substring(0, excuteTime.indexOf(" "));
					int excuteTimes = Integer.parseInt(excuteTime) * 60 * 1000;
					SimpleJobManager.addJob(activityName, ActivityCronExcuteJob.class, 0, excuteTimes, -1);
				} else {
					CronJobManager.addJob(activityName, ActivityCronExcuteJob.class, template.getActivityExecuteTime());
				}
			}
			List<OutboundRecallPolicy> rounds = template.getRoundList();
			recallPolicyDao.deleteOutboundRecallPolicy(template.getDomain(), template.getName());
			for (OutboundRecallPolicy round : rounds) {
				round.setDomain(template.getDomain());
				round.setActivityName(template.getName());
				recallPolicyDao.createOutboundRecallPolicy(round);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "updateActivityInfo fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("delete")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil deleteActivityInfo(ActivityInfo template) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = activityDao.deleteActivityInfo(template);
			if (template.getActivityExecuteType() == 1) {
				String activityName = template.getName() + ":" + template.getDomain();
				SimpleJobManager.removeJob(activityName);
			}
			SimpleJobManager.removeJob(template.getId() + "_back");
			CronJobManager.removeJob(template.getId() + "_back");

			if (ret == true) {
				responseUtil = setResponseUtil(1, "delete Activity Suc", null);
			} else {
				responseUtil = setResponseUtil(0, "delete Activity fail", null);
			}
		} catch (Exception e) {
			responseUtil = setResponseUtil(0, e.getMessage(), null);
			Util.error(this, "deleteActivityInfo fail!", e);
		}
		return responseUtil;
	}

	@POST
	@Path("check")
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseUtil checkName(CheckRequest number) {
		ResponseUtil responseUtil = new ResponseUtil();
		try {
			boolean ret = activityDao.checkName(number.getName(), number.getDomain());
			responseUtil = setResponseUtil(1, ret == true ? "true" : "false", null);
		} catch (Exception e) {
			Util.error(this, "check activity name fail!", e);
			responseUtil = setResponseUtil(0, e.getMessage(), null);
		}
		return responseUtil;
	}

	@POST
	@Path("start")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public ResponseUtil startActivity(RecordId id)
	{
		ActivityInfo template = activityDao.findById("" + id.getId());
		if (template == null)
		{
			ResponseUtil responseUtil = setResponseUtil(0, " Activity not exits", null);
			return responseUtil;
		}
		template.setStatus(1);
		// template.setBeginDatetime(TimeUtil.getCurrentTimeStr());
		// template.setActivityExecuteTime(TimeUtil.getCurrentTimeStr());
		activityDao.updateActivityInfo(template);
		OutboundPolicyInfo info = outboundPolicyInfoDao.getTOutboundPolicyInfos(template.getDomain(),
				template.getPolicyName());
		if (info != null) {
			TaskContainer.policyMap.put(template.getName() + ":" + template.getDomain(), info);
		}
		if (template.getActivityExecuteType() == 0) {

			RosterPool rosterPool = new RosterPool(
					AOUtil.genernateRosterPoolId(template.getDomain(), template.getName()));

			ActivityThread activityThread = new ActivityThread(template, info, rosterPool);
			Thread aThread = new Thread(activityThread,
					AOUtil.genernateActivityThdId(template.getDomain(), template.getName()));
			aThread.start();

			RosterThread rosterThread = new RosterThread(template, rosterPool);
			Thread rThread = new Thread(rosterThread,
					AOUtil.genernateRosterThdId(template.getDomain(), template.getName()));
			rThread.start();
			/**
			 * @author zzj
			 * 修改活动到时间自动停止  修复 活动无法自动停止
			 */
			long now = System.currentTimeMillis();
			long finshTime = TimeUtil.getDateTime(template.getEndDatetime());
			int timeOut = Integer.parseInt((finshTime - now)/1000 + "");
			logger.info((finshTime - now) + "  ms=====>s  " + timeOut);
			if(timeOut > 0) {
				SimpleJobManager.addJob(AOUtil.genernateActivityThdId(template.getDomain(), template.getName()), StopTask.class,timeOut, 0, 0);
			}

		} else if (template.getActivityExecuteType() == 1) {
			PlanRosterPool planRosterPool = new PlanRosterPool(
					AOUtil.genernateRosterPoolId(template.getDomain(), template.getName()));

			PlanActivityThread planActivityThread = new PlanActivityThread(template, info, planRosterPool);
			Thread paThread = new Thread(planActivityThread,
					AOUtil.genernateActivityThdId(template.getDomain(), template.getName()));
			paThread.start();

			PlanRosterThread planRosterThread = new PlanRosterThread(template, planRosterPool);
			Thread prThread = new Thread(planRosterThread,
					AOUtil.genernateRosterThdId(template.getDomain(), template.getName()));
			prThread.start();
		}
		if (template.getActivityExecuteType() == 1) {
			String rosterName = AOUtil.genernateRosterThdId(template.getDomain(), template.getName());
			String excuteTime = template.getActivityExecuteTime();
			if (excuteTime.contains("/")) {
				excuteTime = excuteTime.substring(3, excuteTime.length());
				excuteTime = excuteTime.substring(0, excuteTime.indexOf(" "));
				int excuteTimes = Integer.parseInt(excuteTime) * 60 * 1000;
				int waitTime = Integer.parseInt(excuteTime) * 60;
				SimpleJobManager.removeJob(rosterName);
				SimpleJobManager.addJob(rosterName, ActivityCronExcuteJob.class, waitTime, excuteTimes, -1);
			} else {
				CronJobManager.addJob(rosterName, ActivityCronExcuteJob.class, template.getActivityExecuteTime());
			}
		}
		if (template.getActivityBackAddrType() == 2) {
			String activityName = template.getId() + "_back";
			String excuteTime = template.getActivityBackTime();
			if (excuteTime.contains("/")) {
				excuteTime = excuteTime.substring(3, excuteTime.length());
				excuteTime = excuteTime.substring(0, excuteTime.indexOf(" "));
				int excuteTimes = Integer.parseInt(excuteTime) * 60 * 1000;
				SimpleJobManager.addJob(activityName, RosterExportJob.class, 0, excuteTimes, -1);
			} else {
				CronJobManager.addJob(activityName, RosterExportJob.class, template.getActivityBackTime());
			}
		}

		ResponseUtil responseUtil = setResponseUtil(1, "start Activity Suc", null);
		return responseUtil;
	}

	@POST
	@Path("pause")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil pauseActivity(RecordId id) {
		ActivityInfo template = activityDao.findById("" + id.getId());
		if (template == null) {
			ResponseUtil responseUtil = setResponseUtil(0, " Activity not exits", null);
			return responseUtil;
		}
		String activityName = AOUtil.genernateActivityThdId(template.getDomain(), template.getName());

		if (template.getActivityExecuteType() == 0) {
			ActivityThread aThread = TaskContainer
					.findActivityThread(AOUtil.genernateActivityThdId(template.getDomain(), template.getName()));
			if (aThread != null) {
				aThread.setActivityInfo(template);
				aThread.pauseTask();
			}

			RosterThread rThread = TaskContainer
					.findRosterThread(AOUtil.genernateRosterThdId(template.getDomain(), template.getName()));
			if (rThread != null) {
				rThread.pauseRoster();
			}
		} else if (template.getActivityExecuteType() == 1) {
			PlanActivityThread paThread = TaskContainer.findPlanActivityThread(activityName);
			if (paThread != null) {
				paThread.setActivityInfo(template);
				paThread.pauseTask();
			}

			PlanRosterThread prThread = TaskContainer.findPlanRosterThread(activityName);
			if (prThread != null) {
				prThread.pauseRoster();
			}
		}

		ResponseUtil responseUtil = setResponseUtil(1, "pause PlanActivity Suc", null);
		return responseUtil;
	}

	@POST
	@Path("resume")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResponseUtil resumeActivity(RecordId id) {
		ActivityInfo template = activityDao.findById("" + id.getId());
		if (template == null) {
			ResponseUtil responseUtil = setResponseUtil(0, " Activity not exits", null);
			return responseUtil;
		}
		String activityName = AOUtil.genernateActivityThdId(template.getDomain(), template.getName());
		String rosterName = AOUtil.genernateRosterThdId(template.getDomain(), template.getName());

		if (template.getActivityExecuteType() == 0) {
			ActivityThread aThread = TaskContainer
					.findActivityThread(AOUtil.genernateActivityThdId(template.getDomain(), template.getName()));
			if (aThread != null) {
				OutboundPolicyInfo info = outboundPolicyInfoDao.getTOutboundPolicyInfos(template.getDomain(),
						template.getPolicyName());
				if (info != null) {
					TaskContainer.policyMap.put(template.getName() + ":" + template.getDomain(), info);
				}
				aThread.setActivityInfo(template);
				aThread.setPolicyInfo(info);
				aThread.resumeTask();
			}
			RosterThread rThread = TaskContainer
					.findRosterThread(AOUtil.genernateRosterThdId(template.getDomain(), template.getName()));
			if (rThread != null) {
				rThread.resumeRoster();
			}
			/**
			 * @author zzj
			 * 重启任务时将定时器重新定义防止有修改
			 */
			SimpleJobManager.removeJob(activityName);
			long now = System.currentTimeMillis();
			long finshTime = TimeUtil.getDateTime(template.getEndDatetime());
			int timeOut = Integer.parseInt((finshTime - now)/1000 + "");
			logger.info((finshTime - now) + "  ms=====>s  " + timeOut);
			if(timeOut > 0) {
				SimpleJobManager.addJob(activityName, StopTask.class,timeOut, 0, 0);
			}
		} else if (template.getActivityExecuteType() == 1) {
			PlanActivityThread paThread = TaskContainer.findPlanActivityThread(activityName);
			if (paThread != null) {
				OutboundPolicyInfo info = outboundPolicyInfoDao.getTOutboundPolicyInfos(template.getDomain(),
						template.getPolicyName());
				if (info != null) {
					TaskContainer.policyMap.put(template.getName() + ":" + template.getDomain(), info);
				}
				paThread.setActivityInfo(template);
				paThread.setPolicyInfo(info);
				paThread.resumeTask();
			}

			PlanRosterThread prThread = TaskContainer.findPlanRosterThread(activityName);
			if (prThread != null) {
				prThread.resumeRoster();
			}
		}

		if (template.getActivityExecuteType() == 1) {
			SimpleJobManager.removeJob(rosterName);
			CronJobManager.removeJob(rosterName);
			String excuteTime = template.getActivityExecuteTime();
			if (excuteTime.contains("/")) {
				excuteTime = excuteTime.substring(3, excuteTime.length());
				excuteTime = excuteTime.substring(0, excuteTime.indexOf(" "));
				int excuteTimes = Integer.parseInt(excuteTime) * 60 * 1000;
				SimpleJobManager.addJob(rosterName, ActivityCronExcuteJob.class, 0, excuteTimes, -1);
			} else {
				CronJobManager.addJob(rosterName, ActivityCronExcuteJob.class, template.getActivityExecuteTime());
			}
		}

		if (template.getActivityBackAddrType() == 2) {
			String jobName = template.getId() + "_back";
			String excuteTime = template.getActivityBackTime();
			if (excuteTime.contains("/")) {
				excuteTime = excuteTime.substring(3, excuteTime.length());
				excuteTime = excuteTime.substring(0, excuteTime.indexOf(" "));
				int excuteTimes = Integer.parseInt(excuteTime) * 60 * 1000;
				SimpleJobManager.removeJob(jobName);
				SimpleJobManager.addJob(jobName, RosterExportJob.class, 0, excuteTimes, -1);
			} else {
				CronJobManager.removeJob(jobName);
				CronJobManager.addJob(jobName, RosterExportJob.class, template.getActivityBackTime());
			}
		}

		ResponseUtil responseUtil = setResponseUtil(1, "resume Activity Suc", null);
		return responseUtil;
	}

	@POST
	@Path("stop")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public ResponseUtil stopActivity(RecordId id)
	{
		ActivityInfo template = activityDao.findById("" + id.getId());
		if (template == null)
		{
			ResponseUtil responseUtil = setResponseUtil(0, " Activity not exits", null);
			return responseUtil;
		}
		template.setStatus(3);

		// template.setEndDatetime(TimeUtil.getCurrentTimeStr());
		activityDao.updateActivityInfo(template);
		String activityName = AOUtil.genernateActivityThdId(template.getDomain(), template.getName());
		String rosterName = AOUtil.genernateRosterThdId(template.getDomain(), template.getName());
		if (template.getActivityExecuteType() == 0) {
			ActivityThread aThread = TaskContainer
					.findActivityThread(AOUtil.genernateActivityThdId(template.getDomain(), template.getName()));
			if (aThread != null) {
				aThread.stopTask();
			}
			RosterThread rosterThread = TaskContainer.findRosterThread(rosterName);
			if (rosterThread != null) {
				rosterThread.stopRoster();
				rosterThread.stopRosterStatus();
			}
		} else if (template.getActivityExecuteType() == 1) {
			PlanActivityThread aThread = TaskContainer.findPlanActivityThread(activityName);
			if (aThread != null) {
				aThread.stopTask();
			}
			PlanRosterThread planRosterThread = TaskContainer.findPlanRosterThread(rosterName);
			if (planRosterThread != null) {
				planRosterThread.stopRoster();
				planRosterThread.stopRosterStatus();
			}
			SimpleJobManager.removeJob(activityName);
			CronJobManager.removeJob(activityName);
		}
		ResponseUtil responseUtil = setResponseUtil(1, "stop Activity Suc", null);
		return responseUtil;
	}
}
