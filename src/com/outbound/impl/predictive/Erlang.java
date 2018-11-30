package com.outbound.impl.predictive;

public enum Erlang {
	
	CHINA_TELECOM("erlang-b.csv", 1, "爱尔兰公式");
	
	private String configFileName;// device conf下的默认配置文件名
	private int id;
	private String name;

	private Erlang(String configFileName, int id, String name) {
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
