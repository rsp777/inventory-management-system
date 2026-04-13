package com.pawar.inventory.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
@ApplicationScoped
public class CubiscanToWmsConfig {
	@ConfigProperty(name = "cubiscan.to.wms.topic")
	private String  cubiscanToWmsTopic;

	@ConfigProperty(name = "cubiscan.to.wms.realtime.unassignment.topic")
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
