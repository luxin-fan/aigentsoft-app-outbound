package com.ces.telephonedictionary;

public enum Carrier {
	
	
	
	POOL1("pool1.csv", 1, "号码池1");
	private String configFileName;// device conf下的默认配置文件名
	private int id;
	private String name;

	private Carrier(String configFileName, int id, String name) {
		this.configFileName = configFileName;
		this.id = id;
		this.name = name;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
