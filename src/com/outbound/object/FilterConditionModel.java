package com.outbound.object;

import java.util.List;


/**
 * 过滤对象信息
 *
 */
public class FilterConditionModel {

	private int clear; //1表示清除 0表示不处理
	
	private int removal; //1表示出重 0表示不处理
	
	private List<String> key; //判空校验
	
	private List<String> DNC; //dnc校验

	public int getClear() {
		return clear;
	}

	public void setClear(int clear) {
		this.clear = clear;
	}

	public int getRemoval() {
		return removal;
	}

	public void setRemoval(int removal) {
		this.removal = removal;
	}

	public List<String> getKey() {
		return key;
	}

	public void setKey(List<String> key) {
		this.key = key;
	}

	public List<String> getDNC() {
		return DNC;
	}

	public void setDNC(List<String> dNC) {
		DNC = dNC;
	}
}
