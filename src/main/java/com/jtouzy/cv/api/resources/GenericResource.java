package com.jtouzy.cv.api.resources;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Providers;

import com.jtouzy.cv.api.security.RequestSecurityContext;
import com.jtouzy.dao.DAO;
import com.jtouzy.dao.DAOManager;
import com.owlike.genson.BeanView;
import com.owlike.genson.Genson;

@Produces(MediaType.APPLICATION_JSON)
public class GenericResource {
	@Inject
	protected ContainerRequestContext requestContext;
	@Context
	protected Providers providers;
	
	protected <DA extends DAO<TP>,TP> DA getDAO(Class<DA> daoClass) {
		return DAOManager.getDAO(getRequestContext().getConnection(), daoClass);
	}
	
	protected RequestSecurityContext getRequestContext() {
		// On injecte pas le SecurityContext, car jersey injecte un SecurityContextInjectee et les
		// nouvelles propriétés de notre RequestSecurityContext ne sont pas disponibles
		return (RequestSecurityContext)requestContext.getSecurityContext();
	}
	
	@SafeVarargs
	protected final <T> Response buildViewResponse(Object object, Class<? extends BeanView<T>> firstView, Class<? extends BeanView<?>>... viewClasses) {
		return getBuilderViewResponse(object, firstView, viewClasses).build();
	}
	
	@SafeVarargs
	protected final <T> ResponseBuilder getBuilderViewResponse(Object object, Class<? extends BeanView<T>> firstView, Class<? extends BeanView<?>>... viewClasses) {
		Genson genson = providers.getContextResolver(Genson.class, null).getContext(Object.class);
		return Response.status(Response.Status.OK)
				       .entity(genson.serialize(object, firstView, viewClasses));
	}
}
