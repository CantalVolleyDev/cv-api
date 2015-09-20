package com.jtouzy.cv.api.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jtouzy.cv.api.config.AppConfig;
import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.api.security.RequestSecurityContext;
import com.jtouzy.cv.api.security.TokenHelper;
import com.jtouzy.cv.model.classes.User;
import com.jtouzy.cv.model.dao.UserDAO;
import com.jtouzy.cv.model.errors.UserNotFoundException;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOInstantiationException;

/** PreMatching est nécessaire sinon l'authentification passe avant le RolesAllowedDynamicFeature */
@PreMatching
@Provider
@Priority(3000)
public class AuthFilter implements ContainerRequestFilter {
	private static final Logger logger = LogManager.getLogger(AuthFilter.class);
	
	@Override
	public void filter(ContainerRequestContext requestContext)
	throws IOException {
		initSecurityContext(requestContext);
	}
	
	private void initSecurityContext(ContainerRequestContext requestContext)
	throws IOException {
		try (Connection temporaryConnection = AppConfig.getDataSource().getConnection()) {
			Client client = null;
			RequestSecurityContext securityContext = null;
			String auth = getAuthCookie(requestContext); 
			if (auth != null) {
				String mail = TokenHelper.getUserID(auth);
				User user = null;
				try {
					user = DAOManager.getDAO(temporaryConnection, UserDAO.class).findByMail(mail);
					DAOManager.removeForConnection(temporaryConnection);
				} catch (UserNotFoundException ex) {}
				if (user != null) {
					client = new Client(user);
					logger.trace(new StringBuilder().append("Utilisateur authentifié pour la requête [")
	                        .append(client.getName())
	                        .append("]"));
				} else {
					logger.trace("Clé d'authentification erronée pour la requête");
				}
			} else {
				logger.trace("Aucune authentification d'utilisateur pour la requête");
			}
			securityContext = new RequestSecurityContext(client);
			requestContext.setSecurityContext(securityContext);
		} catch (SQLException | DAOInstantiationException ex) {
			throw new IOException(ex);
		}
	}
	
	private String getAuthCookie(ContainerRequestContext requestContext) {
		Map<String,Cookie> cookies = requestContext.getCookies();
		Optional<Map.Entry<String,Cookie>> opt = cookies.entrySet()
				                                        .stream()
				                                        .filter(k -> k.getKey().equals(Client.AUTHENTIFICATION_COOKIE_NAME))
				                                        .findFirst();
		if (opt.isPresent())
			return opt.get().getValue().getValue();
		return null;
	}
}
