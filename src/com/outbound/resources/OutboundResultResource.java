package com.outbound.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.outbound.common.BaseResource;
import com.outbound.dialer.util.RedisUtil;
import com.outbound.impl.TaskContainer;
import com.outbound.impl.activity.RosterThread;
import com.outbound.impl.metric.MetricUtil;
import com.outbound.impl.plan.PlanRosterThread;
import com.outbound.impl.util.AOUtil;
import com.outbound.impl.util.RosterResultW;
import com.outbound.object.ActivityInfo;
import com.outbound.object.RosterInfo;
import com.outbound.object.dao.ActivityInfoDAO;
import com.outbound.object.dao.RosterInfoDAO;
import com.outbound.object.util.ApplicationContextUtil;
import com.outbound.object.util.TimeUtil;

@Path("/outbound")
public class OutboundResultResource extends BaseResource
{

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	static Gson gson = new Gson();

	private Logger logger = Logger.getLogger(OutboundResultResource.class.getName());

	private RosterInfoDAO rosterDao = (RosterInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("RosterInfoDAO");
	
	private ActivityInfoDAO activityDao = (ActivityInfoDAO) ApplicationContextUtil.getApplicationContext()
			.getBean("ActivityInfoDAO");

	@POST
	@Path("callResult")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public String addCallResult(RosterResultW callresult)
	{

		logger.info("##[" + callresult.getResultType() + "][" + callresult.getResult() + "]["
				+ callresult.getResultCode() + "] receive result " + callresult.toString());

		RosterInfo info = RedisUtil.getRoster(callresult.getActivity_id(), callresult.getBatch_id(),
				callresult.getRosterinfo_id());
		if (info == null)
		{
			logger.info("##roster oubound call result-redis info not exit " + callresult.getRosterinfo_id());
			
			return "fail";
		}
		
		if (callresult.getResultType().equals("outboundEnd"))
		{
			info.setStatus(1);
			info.setCallId(callresult.getCallId());
			info.setMakeCallTime(callresult.getCallTime());
			
			if (info.getCallResult() == null)
			{
				info.setResultCode(callresult.getResultCode());
				info.setCallResult(callresult.getResult());
				if (callresult.getResultCode() == 0)
				{
					info.setCallAnswerTime(TimeUtil.getCurrentTimeStr());
				}
				if (callresult.getResultCode() == 2)
				{
					info.setCallResult("无法接通");
				}
				processCallEnd(info);
				MetricUtil.addCallResult(callresult);
				RedisUtil.updateRoster(info);
				//RedisUtil.expireRoster(info);
			}
			else
			{
				if (info.getResultCode() > 0 && callresult.getResultCode() == 0)
				{
					info.setResultCode(callresult.getResultCode());
					info.setCallResult(callresult.getResult());
					info.setCallAnswerTime(TimeUtil.getCurrentTimeStr());
					processCallEnd(info);
					MetricUtil.addCallResult(callresult);
					RedisUtil.updateRoster(info);
					//RedisUtil.expireRoster(info);
				}
			}
		} 
		else if (callresult.getResultType().equals("callend"))
		{
			info.setStatus(2);
			info.setCallEndTime(callresult.getHangupTime());
			if (info.getCallId() == null || info.getCallId().length() == 0) //无outboundEnd结果推送过来
			{
				info.setCallId(callresult.getCallId());
				info.setMakeCallTime(callresult.getCallTime());
			}
			if (info.getCallResult() == null)
			{
				info.setCallResult("振铃未接听");
				info.setResultCode(11);
				callresult.setResult("振铃未接听");
				callresult.setResultCode(11);
				processCallEnd(info);
				MetricUtil.addCallResult(callresult);
				
			}

			if (callresult.getResultCode() == 0 && info.getCallAnswerTime() != null)
			{
				int answerTime = (int) TimeUtil.getTimeEclipse(info.getCallAnswerTime(), info.getCallEndTime());
				info.setAnswerCallTime(answerTime);
			}
			int callTime = (int) TimeUtil.getTimeEclipse(info.getMakeCallTime(), info.getCallEndTime());
			info.setCallTime(callTime);
			
			rosterDao.createRosterInfo(info);
			
			RedisUtil.delRoster(info);
		}
		
		return "success";
	}
	
	public void processCallEnd(RosterInfo info)
	{
		RosterThread rosterThread = TaskContainer.findRosterThread(AOUtil.genernateRosterThdId(info.getDomain(), info.getActivityName()));
		PlanRosterThread planRosterThread = TaskContainer.findPlanRosterThread(AOUtil.genernateRosterThdId(info.getDomain(), info.getActivityName()));
		
		ActivityInfo activityInfo = activityDao.findActivityInfoByTemplate(info.getTemplateName());
		if(activityInfo.getActivityExecuteType() == 0){
			
			if (rosterThread == null)
			{
				logger.error("### roster oubound call result- recall task start not find roster thread:" + info.getActivityName());
			}
			else
			{
				try
				{
					rosterThread.processCallRoster(info);
				}
				catch(Exception e)
				{
					logger.error("### roster oubound call result- process call roster error:" + e.toString());
				}
				
			}
		}
		if(activityInfo.getActivityExecuteType() == 1){
			
			if (planRosterThread == null)
			{
				logger.error("### plan roster oubound call result- recall task start not find roster thread:" + info.getActivityName());
			}
			else
			{
				try
				{
					planRosterThread.processCallRoster(info);
				}
				catch(Exception e)
				{
					logger.error("### plan roster oubound call result- process call roster error:" + e.toString());
				}
				
			}
		}
	}
}
