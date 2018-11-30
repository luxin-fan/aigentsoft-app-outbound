package com.outbound.object.util;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 结果对象信息
 * @author duanlsh
 *
 */

@XmlRootElement
public class ResponseUtil {
	private int returnCode; //0失败 1成功
	private String returnMsg;
	private Object data;
	
	public ResponseUtil(){
		this.returnCode = 0;
		this.returnMsg = "执行失败";
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
