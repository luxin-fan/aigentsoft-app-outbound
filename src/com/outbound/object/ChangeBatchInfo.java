package com.outbound.object;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ChangeBatchInfo {

	private int oldId;
	private int newId;
	public int getOldId() {
		return oldId;
	}
	public void setOldId(int oldId) {
		this.oldId = oldId;
	}
	public int getNewId() {
		return newId;
	}
	public void setNewId(int newId) {
		this.newId = newId;
	}
	
	
}
