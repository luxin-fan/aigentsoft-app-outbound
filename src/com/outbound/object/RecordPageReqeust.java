package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecordPageReqeust {

	private int startPage;
	private int pageNum;
	private String domain;

	public int getStartPage() {
		return startPage;
	}

	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
