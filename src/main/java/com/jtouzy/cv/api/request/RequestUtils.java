package com.jtouzy.cv.api.request;

import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;

public class RequestUtils {
	public static final String HEADER_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	public static final String HEADER_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	public static final String HEADER_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String HEADER_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String HEADER_ORIGIN = "Origin";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_REQUESTED_WITH = "X-Requested-With";
	public static final String ALLOWED_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
	
	public static final String getOriginHeader(ContainerRequestContext requestContext) {
		List<String> origins = requestContext.getHeaders().get(RequestUtils.HEADER_ORIGIN);
		if (origins != null && origins.size() > 0)
			return origins.get(0);
		return null;
	}
	
	public static final String getDomainOnlyOriginHeader(ContainerRequestContext requestContext) {
		String origin = getOriginHeader(requestContext);
		if (origin == null)
			return null;
		String domain = origin.substring(origin.indexOf("/")+2);
		if (domain.startsWith("www")) {
			domain = domain.substring(domain.indexOf(".")+1);
		}
		if (domain.contains(":")) {
			domain = domain.substring(0, domain.indexOf(":"));
		}
		// Chrome fix...
		if (domain.equals("localhost") || domain.equals("127.0.0.1")) {
			return "";
		}
		return domain;
	}
}
