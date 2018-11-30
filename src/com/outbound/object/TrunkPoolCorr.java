package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TrunkPoolCorr {

	private int id;
	private String trunkPoolName;
	private String displayNum;
	private String domain;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTrunkPoolName() {
		return trunkPoolName;
	}

	public void setTrunkPoolName(String trunkPoolName) {
		this.trunkPoolName = trunkPoolName;
	}

	public String getDisplayNum() {
		return displayNum;
	}

	public void setDisplayNum(String displayNum) {
		this.displayNum = displayNum;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}


}
