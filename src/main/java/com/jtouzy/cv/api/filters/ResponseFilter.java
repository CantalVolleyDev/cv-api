package com.jtouzy.cv.api.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.jtouzy.cv.api.request.HeadersBuilder;
import com.jtouzy.cv.api.security.RequestSecurityContext;
import com.jtouzy.dao.DAOManager;

@Provider
public class ResponseFilter implements ContainerResponseFilter {	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
	throws IOException {
		Map<String,String> headers = new HeadersBuilder().build(requestContext);
		headers.entrySet().forEach(e -> {
			responseContext.getHeaders().add(e.getKey(), e.getValue());
		});
		try {
			RequestSecurityContext securityContext = (RequestSecurityContext)requestContext.getSecurityContext();
			Connection connection = securityContext.getConnection();
			DAOManager.removeForConnection(connection);
			connection.close();
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}
}
