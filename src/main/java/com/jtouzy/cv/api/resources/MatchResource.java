package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.cv.model.errors.UserNotFoundException;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;

@Path("/matchs")
public class MatchResource extends BasicResource<Match, MatchDAO> {
	public MatchResource() {
		super(Match.class, MatchDAO.class);
	}

	@Override
	public List<Match> getAll() 
	throws APIException {
		try {
			return getDAO(MatchDAO.class).getMatchs(getSeasonIDWithParam(), getUserIDWithParam());
		} catch (DAOInstantiationException | QueryException | UserNotFoundException ex) {
			throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, ex);
		}
	}
}
