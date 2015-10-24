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
				String origin = RequestUtils.getOriginHeader(requestContext);
				if (origin != null) {
					List<String> authorized = Splitter.on(',').omitEmptyStrings().splitToList(property);
					Optional<String> opt = authorized.stream().filter(d -> d.equals(origin)).findFirst();
					if (opt.isPresent()) {
						this.headers.put(RequestUtils.HEADER_ALLOW_ORIGIN, opt.get());
						this.headers.put(RequestUtils.HEADER_ALLOW_CREDENTIALS, "true");
					}
				}
			} else {
				this.headers.put(RequestUtils.HEADER_ALLOW_ORIGIN, property);
				this.headers.put(RequestUtils.HEADER_ALLOW_CREDENTIALS, "true");
			}
		}
	}
	
	private void addAllowedRequestHeaders() {
		this.headers.put(RequestUtils.HEADER_ALLOW_HEADERS, new StringBuilder().append(RequestUtils.HEADER_CONTENT_TYPE)
														   					   .append(",")
														   					   .append(RequestUtils.HEADER_CACHE_CONTROL)
														   					   .append(",")
														   					   .append(RequestUtils.HEADER_REQUESTED_WITH)
												   					   	   	   .toString());
		this.headers.put(RequestUtils.HEADER_ALLOW_METHODS, RequestUtils.ALLOWED_METHODS);
	}
}
