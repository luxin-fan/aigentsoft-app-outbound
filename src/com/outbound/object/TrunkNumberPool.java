package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TrunkNumberPool {

	private int id;
	private String name;
	private String descb;
	private String domain;
	private int phonenumCount;
	private int trunkChannels;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescb() {
		return descb;
	}

	public void setDescb(String descb) {
		this.descb = descb;
	}

	public int getTrunkChannels() {
		return trunkChannels;
	}

	public void setTrunkChannels(int trunkChannels) {
		this.trunkChannels = trunkChannels;
	}

	public int getPhonenumCount() {
		return phonenumCount;
	}

	public void setPhonenumCount(int phonenumCount) {
		this.phonenumCount = phonenumCount;
	}

}
