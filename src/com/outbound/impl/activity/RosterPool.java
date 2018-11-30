package com.outbound.impl.activity;

import java.util.LinkedList;
import java.util.Queue;
import com.outbound.common.Util;
import com.outbound.object.Roster;

public class RosterPool {
	private String rosterPoolName;

	private Queue<Roster> callList = new LinkedList<>();
	private Queue<Roster> reCallList = new LinkedList<>();
	private Object lockCallList = new Object();

	public RosterPool(String rosterPoolName) {
		this.rosterPoolName = rosterPoolName;
	}

	public  Roster getRoster() {
		synchronized (lockCallList) {
			if (reCallList.size() == 0 && callList.size() == 0) {
				try {
					lockCallList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			
		}
		if(reCallList.size() != 0 || callList.size() != 0) {
			
			Roster roster = reCallList.poll();
			if (roster == null) {
				roster = callList.poll();
			}
			
			return roster;
		}
		return null;
	}

	public void addCallRoster(Roster roster) {
		synchronized (lockCallList) {
			callList.offer(roster);
			lockCallList.notifyAll();			
		}
	}

	public void addReCallRoster(Roster roster) {
		synchronized (lockCallList) {			
			reCallList.offer(roster);
			lockCallList.notifyAll();
		}
	}

	/**
	 * @author fanlx
	 * @date 2018.10.16
	 */
	public synchronized void removeCallRoster(Roster roster) {
		Util.info("", "recallList的大小为：" + reCallList.size());
		Util.info("", "callList的大小为：" + callList.size());
		if (callList.size() > 0 || reCallList.size() > 0) {
			// Util.info("", "recallList的roster为：" + reCallList.peek().getId());
			Util.info("", "callList的roster为：" + callList.peek().getId());
		}
		if (reCallList.size() > 0 && reCallList.contains(roster)) {
			reCallList.remove(roster);
		}

		if (callList.size() > 0 && callList.contains(roster)) {
			Util.info("", "callList下remove的是：" + roster.getId());
			callList.remove(roster);
		}
		Util.info("", "禁呼后名单池callList大小为：" + callList.size());
	}

	public static void main(String[] args) {
		Queue<Integer> callList = new LinkedList<>();
		callList.offer(1);
		callList.offer(1);
		System.out.println(callList.size());
	}
}
