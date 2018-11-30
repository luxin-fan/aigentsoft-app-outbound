package com.outbound.upenny;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class ActivityCallResultModel {

	private String resultCode;
	
	private List<ActivityCallResultDetailModel> jobResult;

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public List<ActivityCallResultDetailModel> getJobResult() {
		return jobResult;
	}

	public void setJobResult(List<ActivityCallResultDetailModel> jobResult) {
		this.jobResult = jobResult;
	}
	
	
}
