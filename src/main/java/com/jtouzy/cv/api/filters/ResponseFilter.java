package com.jtouzy.cv.api.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import com.google.common.base.Splitter;
import com.jtouzy.cv.api.config.AppConfig;
import com.jtouzy.cv.api.security.RequestSecurityContext;
import com.jtouzy.dao.DAOManager;

@Provider
public class ResponseFilter implements ContainerResponseFilter {	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
	throws IOException {
		String property = AppConfig.getProperty(AppConfig.ORIGIN_ALLOWED);
		if (property != null) {
			if (property.contains(",")) {
				List<String> origins = requestContext.getHeaders().get("Origin");
				if (origins != null && origins.size() > 0) {
					String origin = origins.get(0);
					List<String> authorized = Splitter.on(',').omitEmptyStrings().splitToList(property);
					Optional<String> opt = authorized.stream().filter(d -> d.equals(origin)).findFirst();
					if (opt.isPresent()) {
						responseContext.getHeaders().add("Access-Control-Allow-Origin", opt.get());
					}
				}
				
			} else {
				responseContext.getHeaders().add("Access-Control-Allow-Origin", property);
			}
		}
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
