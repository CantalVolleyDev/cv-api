package com.jtouzy.cv.api.resources;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.jtouzy.cv.api.errors.SeasonNotFoundException;
import com.jtouzy.cv.model.classes.Competition;
import com.jtouzy.cv.model.classes.Season;
import com.jtouzy.cv.model.dao.CompetitionDAO;
import com.jtouzy.cv.model.dao.SeasonDAO;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.query.Query;

@Path("/competitions")
@PermitAll
public class CompetitionResource extends BasicResource<Competition, CompetitionDAO> {
	@QueryParam("seasonId")
	protected String seasonId;
	
	public CompetitionResource() {
		super(CompetitionDAO.class);
	}	
	
	@Override
	protected Query<Competition> query()
	throws DAOInstantiationException, QueryException {
		Query<Competition> query = super.query();
		Integer seasonID = getSeasonIDWithParam();
		if (seasonID != null) {
			query.context().addEqualsCriterion(Competition.SEASON_FIELD, seasonID);
		}
		return query;
	}
	
	private Integer getSeasonIDWithParam()
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
}
