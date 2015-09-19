package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;

@Path("/matchs")
public class MatchResource extends GenericResource {
	@GET
	public List<Match> getMatchs(@QueryParam("seasonId") Integer seasonId,
			                     @QueryParam("userId") Integer userId)
	throws QueryException, DAOInstantiationException {
		return getDAO(MatchDAO.class).getMatchs(seasonId, userId);
	}
}
