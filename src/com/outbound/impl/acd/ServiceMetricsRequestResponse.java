package com.outbound.impl.acd;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServiceMetricsRequestResponse {

	private String requestId;
	private Map<String, ServiceMetricsResponse> serviceMetricsResponseMap = 
			new HashMap<String, ServiceMetricsResponse>();

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Map<String, ServiceMetricsResponse> getServiceMetricsResponseMap() {
		return serviceMetricsResponseMap;
	}

	public void setServiceMetricsResponseMap(
			Map<String, ServiceMetricsResponse> serviceMetricsResponseMap) {
		this.serviceMetricsResponseMap = serviceMetricsResponseMap;
	}

}
