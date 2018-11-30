package com.outbound.impl;

import java.util.TimerTask;
import org.apache.log4j.Logger;

import com.outbound.impl.acd.Agent;

public class SessionTimerTask extends TimerTask {

	private static final Logger log = Logger.getLogger(SessionTimerTask.class
			.getName());

	private String callId;
	private Agent agent;
	
	public SessionTimerTask(String calId, Agent t_agent) {
		callId = calId;
		agent = t_agent;
	}

	public void run() {
		log.info("callId [" + callId + "] timer expired " );
		if(!TaskContainer.callRosterMap.containsKey(callId)){
			return;
		}
		String rosterId = TaskContainer.callRosterMap.get(callId);
		
		String activeId = TaskContainer.callTaskMap.get(callId);
		if(activeId != null){
			//PreOccupiedThread taskThread = TaskContainer.taskMap.get(activeId);
			//if(taskThread != null){
			//	taskThread.resumeTask();
			//}
		}
		
		//AgentUtil.releaseAgent("well.com", agent.getAgent(), agent.getStationId());
		TaskContainer.callRosterMap.remove(callId);
		TaskContainer.callTaskMap.remove(callId);
		TaskContainer.callState.remove(callId);
	}
	
}
