package com.outbound.upenny;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.outbound.request.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 批量获取
 * @author duanlsh
 *
 */
@XmlRootElement
@Data
@EqualsAndHashCode(callSuper=false)
public class UpennyRosterBatchListRequest extends BaseRequest{

	
	private Integer batchId;
	
	private String templateCode; //流程模板编号 对应井星呼叫中心IVR流程编号
	
	private List<BaseUpennyRosterRequest> jobList;

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public List<BaseUpennyRosterRequest> getJobList() {
		return jobList;
	}

	public void setJobList(List<BaseUpennyRosterRequest> jobList) {
		this.jobList = jobList;
	}
}
