package com.outbound.impl.acd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServiceMetricsResponse {

	public enum ServiceMetric {
		ResourceReadyCount, ResourceBusyCount, ResourceStaffedCount, WaitingWorkCount, ProcessingWorkCount, CompletedWorkCount, OldestWorkWaiting, RollingASA, ServiceOccupancy, EWT, Occupancy
	}

	private List<String> attributes = new ArrayList<String>();
	private int priority;
	private Map<String, String> metrics = new HashMap<String, String>();
	private int serviceId;

	public List<String> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Map<String, String> getMetrics() {
		return metrics;
	}

	public void setMetrics(Map<String, String> metrics) {
		this.metrics = metrics;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

}
