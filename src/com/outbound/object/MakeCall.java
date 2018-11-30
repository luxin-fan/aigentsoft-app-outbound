package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MakeCall {

	private String uui;

	private String caller;
	private String callee;
	private String agent;
	private String rosterinfo_id;
	private String trunkGrp;
	private String ani;
	
	private String domain;

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getCallee() {
		return callee;
	}

	public void setCallee(String callee) {
		this.callee = callee;
	}

	public String getUui() {
		return uui;
	}

	public void setUui(String uui) {
		this.uui = uui;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getRosterinfo_id() {
		return rosterinfo_id;
	}

	public void setRosterinfo_id(String rosterinfo_id) {
		this.rosterinfo_id = rosterinfo_id;
	}

	public String getTrunkGrp() {
		return trunkGrp;
	}

	public void setTrunkGrp(String trunkGrp) {
		this.trunkGrp = trunkGrp;
	}

	public String getAni() {
		return ani;
	}

	public void setAni(String ani) {
		this.ani = ani;
	}

}
