package com.outbound.impl.plan;

import java.util.LinkedList;
import java.util.Queue;

import com.outbound.common.Util;
import com.outbound.object.Roster;

public class PlanRosterPool {

	private String rosterPoolName;

	private Queue<Roster> callList = new LinkedList<>();
	private Queue<Roster> reCallList = new LinkedList<>();

	public PlanRosterPool(String rosterPoolName) {
		this.rosterPoolName = rosterPoolName;
	}

	public synchronized Roster getRoster() {
		if (reCallList.size() == 0 && callList.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Roster roster = reCallList.poll();
		if (roster == null) {
			roster = callList.poll();
		}

		return roster;
	}

	public synchronized void addCallRoster(Roster roster) {
		callList.offer(roster);
		notifyAll();
	}

	public synchronized void addReCallRoster(Roster roster) {
		reCallList.offer(roster);
		notifyAll();
	}

	/**
	 * @author fanlx
	 * @date 2018.10.16
	 */
	public synchronized void removeCallRoster(Roster roster) {
		Util.info("", "recallList的大小为：" + reCallList.size());
		Util.info("", "callList的大小为：" + callList.size());
		if (callList.size() > 0 || reCallList.size() > 0) {
			//Util.info("", "recallList的roster为：" + reCallList.peek().getId());
			Util.info("", "callList的roster为：" + callList.peek().getId());
		}
		if (reCallList.size() > 0 && reCallList.contains(roster)) {
			reCallList.remove(roster);
		}

		if (callList.size() > 0 && callList.contains(roster)) {
			callList.remove(roster);
		}
	}
	
}
