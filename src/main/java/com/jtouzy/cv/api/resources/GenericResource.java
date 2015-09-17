package com.jtouzy.cv.api.resources;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.security.RequestSecurityContext;
import com.jtouzy.dao.DAO;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.query.Query;

@Produces(MediaType.APPLICATION_JSON)
public class GenericResource {
	@Inject
	protected ContainerRequestContext requestContext;
	@QueryParam("limitTo")
	protected Integer limitTo;
	@QueryParam("page")
	protected Integer page;
	
	protected void manageParams(Query<?> query)
	throws APIException {
		Integer offset = null;
		if (limitTo != null && page != null && page > 1) {
			offset = limitTo * (page-1);
		}
		query.context()
		     .limitTo(limitTo)
		     .offset(offset);		
	}	
	
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
