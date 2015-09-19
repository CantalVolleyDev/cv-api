package com.jtouzy.cv.api.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;

import com.google.common.base.Splitter;
import com.jtouzy.cv.api.config.AppConfig;

public class HeadersBuilder {
	private Map<String, String> headers;
	
	public HeadersBuilder() {
		this.headers = new HashMap<>();
	}
	
	public Map<String,String> build(ContainerRequestContext requestContext) {
		addAllowedOrigins(requestContext);
		addAllowedRequestHeaders();
		return this.headers;
	}
	
	private void addAllowedOrigins(ContainerRequestContext requestContext) {
		String property = AppConfig.getProperty(AppConfig.ORIGIN_ALLOWED);
		if (property != null) {
			if (property.contains(",")) {
				List<String> origins = requestContext.getHeaders().get("Origin");
				if (origins != null && origins.size() > 0) {
					String origin = origins.get(0);
					List<String> authorized = Splitter.on(',').omitEmptyStrings().splitToList(property);
					Optional<String> opt = authorized.stream().filter(d -> d.equals(origin)).findFirst();
					if (opt.isPresent()) {
						this.headers.put("Access-Control-Allow-Origin", opt.get());
						this.headers.put("Access-Control-Allow-Credentials", "true");
					}
				}
				
			} else {
				this.headers.put("Access-Control-Allow-Origin", property);
				this.headers.put("Access-Control-Allow-Credentials", "true");
			}
		}
	}
	
	private void addAllowedRequestHeaders() {
		this.headers.put("Access-Control-Allow-Headers", "Content-Type");
	}
}
