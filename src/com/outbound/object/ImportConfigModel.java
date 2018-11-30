package com.outbound.object;

import lombok.Getter;
import lombok.Setter;


/**
 * 导入配置信息
 * @author duanlsh
 *
 */
@Getter
@Setter
public class ImportConfigModel {

	private String type;  //ftp file oracle mysql sqlserver
	/**
	 * 服务器
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
	private int p = 21;
	
	/**
	 * 数据库名字
	 */
	private String name; 
	
	/**
	 * 服务器文件地址
	 */
	private String path;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
