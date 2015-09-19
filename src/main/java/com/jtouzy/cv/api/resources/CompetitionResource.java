package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.api.errors.SeasonNotFoundException;
import com.jtouzy.cv.model.classes.Championship;
import com.jtouzy.cv.model.classes.Competition;
import com.jtouzy.cv.model.dao.CompetitionDAO;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.model.ColumnContextNotFoundException;
import com.jtouzy.dao.errors.model.FieldContextNotFoundException;
import com.jtouzy.dao.errors.model.TableContextNotFoundException;
import com.jtouzy.dao.query.Query;

@Path("/competitions")
@PermitAll
public class CompetitionResource extends BasicResource<Competition, CompetitionDAO> {
	@QueryParam("fillChampionships")
	protected Boolean fillChampionships;
	
	public CompetitionResource() {
		super(Competition.class, CompetitionDAO.class);
	}
	
	@Override
	public List<Competition> getAll() 
	throws APIException {
		try {
			if (fillChampionships != null && fillChampionships) {
				return queryCollection(Championship.class).fill();
			}
			return super.getAll();
		} catch (TableContextNotFoundException | ColumnContextNotFoundException | FieldContextNotFoundException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@Override
	protected void manageParams(Query<?> query)
	throws APIException {
		try {
			super.manageParams(query);
			Integer seasonID = getSeasonIDWithParam();
			if (seasonID != null) {
				query.context().addEqualsCriterion(Competition.TABLE, Competition.SEASON_FIELD, seasonID);
			}
		} catch (SeasonNotFoundException | DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
}
