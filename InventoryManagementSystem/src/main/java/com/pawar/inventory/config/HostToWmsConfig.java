package com.pawar.inventory.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
@ApplicationScoped
public class HostToWmsConfig {
	@ConfigProperty(name = "wms.item.data.incoming")
	private String  wmsItemDataIncoming;

	public String getWmsItemDataIncoming() {
		return wmsItemDataIncoming;
	}
}
