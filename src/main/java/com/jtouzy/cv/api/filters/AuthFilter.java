package com.jtouzy.cv.api.filters;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.api.security.RequestSecurityContext;

/** PreMatching est nécessaire sinon l'authentification passe avant le RolesAllowedDynamicFeature */
@PreMatching
@Provider
public class AuthFilter implements ContainerRequestFilter {
	private static final Logger logger = LogManager.getLogger(AuthFilter.class);
	
	@Override
	public void filter(ContainerRequestContext requestContext)
	throws IOException {
		initSecurityContext(requestContext);
	}
	
	private void initSecurityContext(ContainerRequestContext requestContext)
	throws IOException {
		try {
			Client client = null;
			RequestSecurityContext securityContext = null;
			String auth = requestContext.getHeaderString("X-CVApi-Auth"); 
			if (auth != null) {
				client = new Client(auth);
				securityContext = new RequestSecurityContext(client);
				logger.trace(new StringBuilder().append("Utilisateur authentifié pour la requête [")
						                        .append(auth)
						                        .append("]"));
			} else {
				securityContext = new RequestSecurityContext();
				logger.trace("Aucune authentification d'utilisateur pour la requête");
			}
			requestContext.setSecurityContext(securityContext);
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}
}
