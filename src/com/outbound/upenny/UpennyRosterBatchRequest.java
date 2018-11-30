package com.outbound.upenny;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 批量呼叫任务请求对象
 * @author duanlsh
 *
 */
@XmlRootElement
public class UpennyRosterBatchRequest extends UpennyRosterRequest{

	
	private String accessKey; // OSS账号
	private String accessKeySecret; //OSS账号对应密钥值
	private String bucket; //OSS桶名 批量文件所在OSS桶名
	private String batchId; //批次编号
	private String objectName; //批量文件文件名  批量文件文件名，井星可根据bucket + objectName获取批量文件
	
	
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getAccessKeySecret() {
		return accessKeySecret;
	}
	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}
	public String getBucket() {
		return bucket;
	}
	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	
	
}
