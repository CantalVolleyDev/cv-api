package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.api.errors.SeasonNotFoundException;
import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.model.classes.Season;
import com.jtouzy.cv.model.dao.SeasonDAO;
import com.jtouzy.cv.model.errors.UserNotFoundException;
import com.jtouzy.dao.DAO;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOCrudException;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.NullUniqueIndexException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.SQLExecutionException;
import com.jtouzy.dao.errors.model.ColumnContextNotFoundException;
import com.jtouzy.dao.errors.model.FieldContextNotFoundException;
import com.jtouzy.dao.errors.model.TableContextNotFoundException;
import com.jtouzy.dao.query.Query;
import com.jtouzy.dao.query.QueryCollection;

@Produces(MediaType.APPLICATION_JSON)
public class BasicResource<T, D extends DAO<T>> extends GenericResource {
	private Class<D> daoClass;
	private Class<T> objectClass;
	@QueryParam("seasonId")
	protected String seasonId;
	@QueryParam("userId")
	protected String userId;
	
	public BasicResource(Class<T> objectClass, Class<D> daoClass) {
		this.daoClass = daoClass;
		this.objectClass = objectClass;
	}

	@GET
	public List<T> getAll()
	throws APIException {
		try {
			return query().many();
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
	
	protected Query<T> query()
	throws DAOInstantiationException, QueryException {
		Query<T> query = DAOManager.getDAO(getRequestContext().getConnection(), daoClass).query();
		manageParams(query);
		return query;
	}
	
	protected <C> QueryCollection<T,C> queryCollection(Class<C> collectionClass)
	throws TableContextNotFoundException, ColumnContextNotFoundException, FieldContextNotFoundException {
		QueryCollection<T,C> query = QueryCollection.build(getRequestContext().getConnection(), this.objectClass, collectionClass);
		manageParams(query);
		return query;
	}
	
	protected Integer getSeasonIDWithParam()
	throws SeasonNotFoundException, DAOInstantiationException, QueryException {
		if (seasonId != null) {
			Integer seasonID = null;
			if (seasonId.equals("current")) {
				Season current = DAOManager.getDAO(getRequestContext().getConnection(), SeasonDAO.class).getCurrentSeason();
				if (current == null)
					throw new SeasonNotFoundException();
				seasonID = current.getIdentifier();
			} else {
				try {
					seasonID = Integer.parseInt(seasonId);
				} catch (NumberFormatException ex) {
					throw new SeasonNotFoundException();
				}
			}
			return seasonID;
		}
		return null;
	}
	
	protected Integer getUserIDWithParam()
	throws UserNotFoundException {
		if (userId != null) {
			Integer userID = null;
			if (userId.equals("current")) {
				Client client = getRequestContext().getUserPrincipal();
				if (client == null) {
					throw new UserNotFoundException("Aucun utilisateur connecté");
				}
				userID = client.getUser().getIdentifier();
			} else {
				try {
					userID = Integer.parseInt(userId);
				} catch (NumberFormatException ex) {
					throw new UserNotFoundException();
				}
			}
			return userID;
		}
		return null;
	}
}
