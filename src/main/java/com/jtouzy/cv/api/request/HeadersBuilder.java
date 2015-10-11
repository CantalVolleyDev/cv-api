package com.jtouzy.cv.api.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;

import com.google.common.base.Splitter;
import com.jtouzy.cv.config.PropertiesNames;
import com.jtouzy.cv.config.PropertiesReader;

public class HeadersBuilder {
	private static final String ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final String ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	private static final String ALLOW_HEADERS = "Access-Control-Allow-Headers";
	private static final String ALLOW_METHODS = "Access-Control-Allow-Methods";
	
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	private static final String HEADER_ORIGIN = "Origin";
	private static final String HEADER_ALLOWED_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
	
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
		String property = PropertiesReader.getProperty(PropertiesNames.ORIGIN_ALLOWED);
		if (property != null) {
			if (property.contains(",")) {
				List<String> origins = requestContext.getHeaders().get(HEADER_ORIGIN);
				if (origins != null && origins.size() > 0) {
					String origin = origins.get(0);
					List<String> authorized = Splitter.on(',').omitEmptyStrings().splitToList(property);
					Optional<String> opt = authorized.stream().filter(d -> d.equals(origin)).findFirst();
					if (opt.isPresent()) {
						this.headers.put(ALLOW_ORIGIN, opt.get());
						this.headers.put(ALLOW_CREDENTIALS, "true");
					}
				}
				
			} else {
				this.headers.put(ALLOW_ORIGIN, property);
				this.headers.put(ALLOW_CREDENTIALS, "true");
			}
		}
	}
	
	private void addAllowedRequestHeaders() {
		this.headers.put(ALLOW_HEADERS, HEADER_CONTENT_TYPE);
		this.headers.put(ALLOW_METHODS, HEADER_ALLOWED_METHODS);
	}
}
