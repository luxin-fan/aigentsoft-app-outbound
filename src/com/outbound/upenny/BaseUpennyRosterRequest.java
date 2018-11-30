package com.outbound.upenny;

import javax.xml.bind.annotation.XmlRootElement;

import com.outbound.request.BaseRequest;

/**
 * UpennyRoster 请求参数
 * @author duanlsh
 *
 */
@XmlRootElement
 public class BaseUpennyRosterRequest  extends BaseRequest{
	
	 
	private String jobId; //呼叫Id 由senseInfo 提供，此ID与 呼叫结果相对应
	private Long phone; //手机号码  需要拨打的手机号码
	private UserInfoModel jobData;
	
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public Long getPhone() {
		return phone;
	}
	public void setPhone(Long phone) {
		this.phone = phone;
	}
	public UserInfoModel getJobData() {
		return jobData;
	}
	public void setJobData(UserInfoModel jobData) {
		this.jobData = jobData;
	}
	 
	 
}

