package com.outbound.impl.acd;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServiceMetricsRequest {

	private String id;
	private HashMap<String, ServiceMetrics> serviceMap = new HashMap<String, ServiceMetrics>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public HashMap<String, ServiceMetrics> getServiceMap() {
		return serviceMap;
	}

	public void setServiceMap(HashMap<String, ServiceMetrics> serviceMap) {
		this.serviceMap = serviceMap;
	}
}
