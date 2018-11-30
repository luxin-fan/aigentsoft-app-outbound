package com.outbound.upenny;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * UpennyRoster 请求参数
 * @author duanlsh
 *
 */
@XmlRootElement
 public class UpennyRosterRequest extends BaseUpennyRosterRequest{
	
	private String templateCode; //流程模板编号 对应井星呼叫中心IVR流程编号
	private String callbackUrl; //井星完成呼叫工作后，可调用此接口将工单结果返回给senseInfo
	
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	public String getCallbackUrl() {
		return callbackUrl;
	}
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

}

