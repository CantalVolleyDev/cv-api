package com.jtouzy.cv.api.filters;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.api.security.RequestSecurityContext;

/** PreMatching est nécessaire sinon l'authentification passe avant le RolesAllowedDynamicFeature */
@PreMatching
@Provider
public class AuthFilter implements ContainerRequestFilter {
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
				System.out.println("Utilisateur : " + auth);
			} else {
				securityContext = new RequestSecurityContext();
				System.out.println("Aucune authentification");
			}
			requestContext.setSecurityContext(securityContext);
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}
}
