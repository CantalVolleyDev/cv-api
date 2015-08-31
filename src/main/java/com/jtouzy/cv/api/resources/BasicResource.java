package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.container.ContainerRequestContext;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.api.security.RequestSecurityContext;
import com.jtouzy.dao.DAO;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOCrudException;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.NullUniqueIndexException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.SQLExecutionException;

public class BasicResource<T, D extends DAO<T>> {
	@Inject
	protected ContainerRequestContext requestContext;
	private Class<D> daoClass;
	
	public BasicResource(Class<D> daoClass) {
		this.daoClass = daoClass;
	}

	@GET
	public List<T> getAll()
	throws APIException {
		try {
			return DAOManager.getDAO(getRequestContext().getConnection(), daoClass).queryAll();
		} catch (DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@POST
	@RolesAllowed("admin")
	public T create(@NotNull T object)
	throws APIException {
		try {
			return DAOManager.getDAO(getRequestContext().getConnection(), daoClass).create(object);
		} catch (DAOInstantiationException | SQLExecutionException | DAOCrudException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@PUT
	@RolesAllowed("admin")
	public T update(@NotNull T object)
	throws APIException {
		try {
			return DAOManager.getDAO(getRequestContext().getConnection(), daoClass).update(object);
		} catch (DAOInstantiationException | SQLExecutionException | DAOCrudException | NullUniqueIndexException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@DELETE
	@RolesAllowed("admin")
	public void delete(@NotNull T object)
	throws APIException {
		try {
			DAOManager.getDAO(getRequestContext().getConnection(), daoClass).delete(object);
		} catch (DAOInstantiationException | SQLExecutionException | DAOCrudException | NullUniqueIndexException ex) {
			throw new ProgramException(ex);
		}
	}
	
	protected RequestSecurityContext getRequestContext() {
		/** Ne pas récupérer directement en injectant le SecurityContext, car Jersey injecte un SecurityContextInjectee */
		return (RequestSecurityContext)requestContext.getSecurityContext();
	}
	
}
