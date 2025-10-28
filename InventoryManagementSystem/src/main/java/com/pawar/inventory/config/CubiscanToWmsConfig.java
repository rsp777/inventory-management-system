package com.pawar.inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CubiscanToWmsConfig {
	@Value("${cubiscan.to.wms.topic}")
	private String  cubiscanToWmsTopic;

	@Value("${cubiscan.to.wms.realtime.unassignment.topic}")
	private String cubiscanToWmsRealtimeUnassignmentTopic;
	

	public String getCubiscanToWmsTopic() {
		return cubiscanToWmsTopic;
	}

	public void setCubiscanToWmsTopic(String cubiscanToWmsTopic) {
		this.cubiscanToWmsTopic = cubiscanToWmsTopic;
	}

	public String getCubiscanToWmsRealtimeUnAssignmentTopic() {
		return cubiscanToWmsRealtimeUnassignmentTopic;
	}

	public void setCubiscanToWmsRealtimeUnAssignmentTopic(String cubiscanToWmsRealtimeUnassignmentTopic) {
		this.cubiscanToWmsRealtimeUnassignmentTopic = cubiscanToWmsRealtimeUnassignmentTopic;
	}
}