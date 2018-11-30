package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TrunkNumber {

	private int id;
	private String displayNum;
	private String trunkGrp;
	private String domain;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTrunkGrp() {
		return trunkGrp;
	}

	public void setTrunkGrp(String trunkGrp) {
		this.trunkGrp = trunkGrp;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDisplayNum() {
		return displayNum;
	}

	public void setDisplayNum(String displayNum) {
		this.displayNum = displayNum;
	}

}
