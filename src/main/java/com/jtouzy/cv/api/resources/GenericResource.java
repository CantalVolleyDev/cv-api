package com.jtouzy.cv.api.resources;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;

import com.jtouzy.cv.api.security.RequestSecurityContext;
import com.jtouzy.dao.DAO;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOInstantiationException;

@Produces(MediaType.APPLICATION_JSON)
public class GenericResource {
	@Inject
	protected ContainerRequestContext requestContext;
	
	protected <DA extends DAO<TP>,TP> DA getDAO(Class<DA> daoClass)
	throws DAOInstantiationException {
		return DAOManager.getDAO(getRequestContext().getConnection(), daoClass);
	}
	
	protected RequestSecurityContext getRequestContext() {
		// On injecte pas le SecurityContext, car jersey injecte un SecurityContextInjectee et les
		// nouvelles propriétés de notre RequestSecurityContext ne sont pas disponibles
		return (RequestSecurityContext)requestContext.getSecurityContext();
	}
}
