package com.pawar.inventory.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HostToWmsConfig {
	@Value("${wms.item.data.incoming}")
	private String  wmsItemDataIncoming;

	public String getWmsItemDataIncoming() {
		return wmsItemDataIncoming;
	}
}