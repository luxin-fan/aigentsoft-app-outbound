package com.outbound.dialer.util;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 数据库
 * @author duanlsh
 *
 */
@AllArgsConstructor
@Getter
public enum DBENUM {

	//jdbc:mysql://192.168.211.5:3306/jdbcdemo
	MYSQL("MYSQL", "com.mysql.jdbc.Driver", "jdbc:mysql://{0}:{1}/{2}"),
	//jdbc:oracle:thin:@127.0.0.1:1521:dbname
	ORACLE("ORACLE", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@{0}:{1}:{2}"),
	//jdbc:sqlserver://localhost:1433; DatabaseName=dbname
	SQLSERVER("SQLSERVER", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://{0}:{1}; DatabaseName={2}");
	
	private String dbType;
	private String driver;
	private String url;
	
	DBENUM(String param1, String param2, String param3){
		dbType = param1;
		driver = param2;
		url = param3;
	}
	
	private static final Map<String, DBENUM> dbMap = new HashMap<>();
	
	static {
		for (DBENUM dbEnum : values()){
			dbMap.put(dbEnum.dbType, dbEnum);
		}
	}
	
	public static DBENUM getInstance(String dbType){
		return dbMap.get(dbType.toUpperCase());
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
