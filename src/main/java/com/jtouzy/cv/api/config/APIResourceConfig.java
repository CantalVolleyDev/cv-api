package com.jtouzy.cv.api.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

public class APIResourceConfig extends ResourceConfig {
	public APIResourceConfig() {
		register(RolesAllowedDynamicFeature.class);
	}
}
