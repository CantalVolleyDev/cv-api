package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.api.errors.SeasonNotFoundException;
import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.api.security.Roles;
import com.jtouzy.cv.model.classes.Season;
import com.jtouzy.cv.model.dao.SeasonDAO;
import com.jtouzy.dao.DAO;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOCrudException;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.validation.DataValidationException;

@Produces(MediaType.APPLICATION_JSON)
public class BasicResource<T, D extends DAO<T>> extends GenericResource {
	private Class<D> daoClass;
	@QueryParam("season")
	protected String seasonId;
	@QueryParam("user")
	protected String userId;
	
	public BasicResource(Class<T> objectClass, Class<D> daoClass) {
		this.daoClass = daoClass;
		/* FIXME: objectClass ne sert plus */
	}

	@GET
	public List<T> getAll()
	throws APIException {
		try {
			return getDAO().getAll();
		} catch (DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@POST
	@RolesAllowed(Roles.ADMIN)
	public T create(@NotNull T object)
	throws APIException, DataValidationException {
		try {
			return getDAO().create(object);
		} catch (DAOInstantiationException | DAOCrudException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@PUT
	@RolesAllowed(Roles.ADMIN)
	public T update(@NotNull T object)
	throws APIException, DataValidationException {
		try {
			return getDAO().update(object);
		} catch (DAOInstantiationException | DAOCrudException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@DELETE
	@RolesAllowed(Roles.ADMIN)
	public void delete(@NotNull T object)
	throws APIException, DataValidationException {
		try {
			getDAO().delete(object);
		} catch (DAOInstantiationException | DAOCrudException ex) {
			throw new ProgramException(ex);
		}
	}
	
	protected D getDAO()
	throws DAOInstantiationException {
		return getDAO(this.daoClass);
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
	
	protected Integer getUserIDWithParam() {
		if (userId != null) {
			Integer userID = null;
			if (userId.equals("current")) {
				Client client = getRequestContext().getUserPrincipal();
				if (client == null) {
					throw new NotAuthorizedException("Aucun utilisateur connecté", "");
				}
				userID = client.getUser().getIdentifier();
			} else {
				try {
					userID = Integer.parseInt(userId);
				} catch (NumberFormatException ex) {
					throw new BadRequestException(ex);
				}
			}
			return userID;
		}
		return null;
	}
}
