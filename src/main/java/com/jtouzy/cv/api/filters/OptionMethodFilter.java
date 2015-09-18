package com.jtouzy.cv.api.filters;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@PreMatching
@Provider
public class OptionMethodFilter implements ContainerRequestFilter {
	@Override
	public void filter(ContainerRequestContext requestContext) 
	throws IOException {
		if (requestContext.getMethod().equals("OPTIONS")) {
			requestContext.abortWith(Response.status(Response.Status.OK)
					                         .build());
		}
	}
}
