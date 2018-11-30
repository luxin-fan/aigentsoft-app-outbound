package com.outbound.request;

import lombok.Data;

/**
 * 链接测试对象
 * @author duanlsh
 *
 */
@Data
public class DBTestRequest extends BaseRequest{

	private Short serverType; //导入类别 0 手动 1、ftp 2、db 3、url
	/**
	 * Ftp服务器
	 */
	private String server;
	/**
	 * 用户名
	 */
	private String uname;
	/**
	 * 密码
	 */
	private String pwd;
	/**
	 * 连接端口，默认21
	 */
	private int port = 21;
	
	/**
	 * 服务器文件地址
	 */
	private String remotePath;
	
	private String dbType; //数据库类型 mysql
	private String dbName; //数据库名
	public Short getServerType() {
		return serverType;
	}
	public void setServerType(Short serverType) {
		this.serverType = serverType;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public String getDbType() {
		return dbType;
	}
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}
