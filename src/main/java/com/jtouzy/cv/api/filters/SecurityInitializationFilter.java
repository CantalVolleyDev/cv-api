package com.jtouzy.cv.api.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import com.jtouzy.cv.api.security.RequestSecurityContext;

@PreMatching
@Provider
@Priority(1000)
public class SecurityInitializationFilter implements ContainerRequestFilter {
	@Override
	public void filter(ContainerRequestContext requestContext) 
	throws IOException {
		requestContext.setSecurityContext(new RequestSecurityContext());
	}
}
