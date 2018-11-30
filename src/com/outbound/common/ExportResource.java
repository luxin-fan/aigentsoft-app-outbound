package com.outbound.common;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class ExportResource {
	private PageRequest pageRequest;
	private List<String> title;
	private List<String> condition;
	private String table_title;
	
	public String getTable_title() {
		return table_title;
	}
	public void setTable_title(String table_title) {
		this.table_title = table_title;
	}
	public PageRequest getPageRequest() {
		return pageRequest;
	}
	public void setPageRequest(PageRequest pageRequest) {
		this.pageRequest = pageRequest;
	}
	public List<String> getTitle() {
		return title;
	}
	public void setTitle(List<String> title) {
		this.title = title;
	}
	public List<String> getCondition() {
		return condition;
	}
	public void setCondition(List<String> condition) {
		this.condition = condition;
	}
	
}
