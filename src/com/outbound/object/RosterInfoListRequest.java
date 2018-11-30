package com.outbound.object;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement
public class RosterInfoListRequest {
	
	private int type;

	private List<String> rosterIdList; 
	
	private String batchName; //批次名字
	
	private String domain; //domain

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getRosterIdList() {
		return rosterIdList;
	}

	public void setRosterIdList(List<String> rosterIdList) {
		this.rosterIdList = rosterIdList;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
}
