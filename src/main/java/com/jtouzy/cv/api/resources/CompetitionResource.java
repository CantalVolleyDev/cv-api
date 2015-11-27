package com.jtouzy.cv.api.resources;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.model.classes.Competition;
import com.jtouzy.cv.model.dao.CompetitionDAO;
import com.jtouzy.dao.errors.QueryException;

@Path("/competitions")
@PermitAll
public class CompetitionResource extends BasicResource<Competition, CompetitionDAO> {
	@QueryParam("fillChampionships")
	protected Boolean fillChampionships;
	
	public CompetitionResource() {
		super(CompetitionDAO.class);
	}
	
	@Override
	public List<Competition> getAll() 
	throws APIException {
		try {
			if (fillChampionships != null && fillChampionships) {
				return getDAO().getAllWithChampionshipsBySeason(getSeasonIDWithParam());
			}
			return super.getAll();
		} catch (QueryException ex) {
			throw new ProgramException(ex);
		}
	}
}
