package com.jtouzy.cv.api.resources;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.classes.MatchPlayer;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.cv.model.dao.MatchPlayerDAO;
import com.jtouzy.cv.model.dao.SeasonTeamPlayerDAO;
import com.jtouzy.cv.model.errors.MatchNotFoundException;
import com.jtouzy.cv.model.errors.UserNotFoundException;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;

@Path("/matchs")
public class MatchResource extends BasicResource<Match, MatchDAO> {
	public MatchResource() {
		super(Match.class, MatchDAO.class);
	}

	@GET
	@Path("/{id}/submitInfos")
	public MatchSubmitInfos getMatchSubmitInfos(@PathParam("id") Integer matchId)
	throws MatchNotFoundException, ProgramException {
		try {
			// Lecture du match avec détails (Exception si le match n'existe pas)
			Match match = getDAO().queryOneWithDetails(matchId);
			// Select unique pour récupérer tous les joueurs des 2 équipes
			// On fait ensuite un tri pour diviser, à la place de faire 2 select
			List<SeasonTeamPlayer> allPlayers = 
					getDAO(SeasonTeamPlayerDAO.class).getPlayers(
							match.getChampionship().getCompetition().getSeason().getIdentifier(), 
							Arrays.asList(match.getFirstTeam().getIdentifier(), 
									      match.getSecondTeam().getIdentifier()));
			List<MatchPlayer> matchPlayers = getDAO(MatchPlayerDAO.class).getPlayers(matchId);
			// Création des informations du match
			MatchSubmitInfos infos = new MatchSubmitInfos();
			infos.setMatch(match);
			infos.setFirstTeamPlayers(allPlayers.stream()
					                            .filter(s -> s.getTeam().getIdentifier() == match.getFirstTeam().getIdentifier())
					                            .collect(Collectors.toList()));
			infos.setSecondTeamPlayers(allPlayers.stream()
					                             .filter(s -> s.getTeam().getIdentifier() == match.getSecondTeam().getIdentifier())
					                             .collect(Collectors.toList()));
			infos.setFirstTeamMatchPlayers(matchPlayers.stream()
					                                   .filter(p -> p.getTeam().getIdentifier() == match.getFirstTeam().getIdentifier())
					                                   .collect(Collectors.toList()));
			infos.setSecondTeamMatchPlayers(matchPlayers.stream()
                                                        .filter(p -> p.getTeam().getIdentifier() == match.getSecondTeam().getIdentifier())
                                                        .collect(Collectors.toList()));
			return infos;
		} catch (DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
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
