package com.jtouzy.cv.api.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class RequestFilter implements ContainerRequestFilter {
	@Override
	public void filter(ContainerRequestContext requestContext) 
	throws IOException {
		StringBuilder requestLog = new StringBuilder();
		requestLog.append("REQUESTLOG[")
			      .append(requestContext.getDate())
		          .append("] - ")
		          .append(requestContext.getUriInfo().getPath())
		          .append(" - ")
		          .append(requestContext.getHeaders())
		          .append(" / ")
		          .append(requestContext.getSecurityContext());
		System.out.println(requestLog);
	}
}
