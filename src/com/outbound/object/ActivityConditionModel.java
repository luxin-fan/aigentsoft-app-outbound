package com.outbound.object;

import lombok.Getter;
import lombok.Setter;



/**
 * 条件对象信息
 *
 */
@Setter
@Getter
public class ActivityConditionModel {

	private String name;
	
	private String type;
	
	private String symbol;
	
	private String condition;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
