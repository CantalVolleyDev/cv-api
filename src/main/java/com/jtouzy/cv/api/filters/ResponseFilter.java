package com.jtouzy.cv.api.filters;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.jtouzy.cv.api.config.AppConfig;
import com.jtouzy.cv.api.security.RequestSecurityContext;

@Provider
public class ResponseFilter implements ContainerResponseFilter {	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
	throws IOException {
		String property = AppConfig.getProperty(AppConfig.ORIGIN_ALLOWED);
		if (property != null) {
			responseContext.getHeaders().add("Access-Control-Allow-Origin", property);
		}
		try {
			RequestSecurityContext securityContext = (RequestSecurityContext)requestContext.getSecurityContext();
			securityContext.getConnection().close();
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}
}
