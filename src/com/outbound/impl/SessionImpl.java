package com.outbound.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.apache.log4j.Logger;

import com.outbound.impl.acd.Agent;

public class SessionImpl {

	private static final Logger log = Logger.getLogger(SessionImpl.class
			.getName());

	protected static int _defaultSessionTimeout = 60;
	private Timer _timerThread  = new Timer("Session Object Timer Thread");
	private Map<String, SessionTimerTask> timerTaskMap = new HashMap<String, SessionTimerTask>();
	
	public void scheduleSessionTimeout(int secs, String callId, Agent agent) {

		log.info("scheduleSessionTimeout for callId " + callId +" - " 
		+secs + " - " + agent.getAgent());
		cancelSessionTimeout(callId);
		SessionTimerTask _sessionTimerTask = new SessionTimerTask(callId, agent);
		timerTaskMap.put(callId, _sessionTimerTask);
		try {
			this._timerThread.schedule(_sessionTimerTask, secs * 1000);
		} catch (IllegalStateException e) {

			log.warn("IllegalStateException from timer, restarting timer", e);

			this._timerThread = new Timer("Session Object Timer Thread");
			_sessionTimerTask.cancel();
			_sessionTimerTask = new SessionTimerTask(callId, agent);

			this._timerThread.schedule(_sessionTimerTask, secs * 1000);
		}
	}

	public void cancelSessionTimeout(String callId) {
		SessionTimerTask _sessionTimerTask = timerTaskMap.get( callId);
		if (_sessionTimerTask != null) {
			log.info("cancelSessionTimeout for callId " + callId );
			_sessionTimerTask.cancel();
			_sessionTimerTask = null;
			timerTaskMap.remove( callId);
		}
	}

}
