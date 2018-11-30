package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Holiday {

	private int id;
	private String startDate;
	private String endDate;
	// 0 open 1 inwork 2 completed
	private int holidayType;
	private String domain;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getHolidayType() {
		return holidayType;
	}

	public void setHolidayType(int holidayType) {
		this.holidayType = holidayType;
	}

}
