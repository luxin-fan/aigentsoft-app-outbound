package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class AutoImportConfigModel {

	
	private String source; //db ftp file
	
	private ImportConfigModel context;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public ImportConfigModel getContext() {
		return context;
	}

	public void setContext(ImportConfigModel context) {
		this.context = context;
	} 
}
