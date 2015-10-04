package com.jtouzy.cv.api.resources;

import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jtouzy.cv.model.classes.Championship;
import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.dao.ChampionshipDAO;
import com.jtouzy.cv.model.errors.CalendarGenerationException;
import com.jtouzy.cv.model.errors.RankingsCalculateException;
import com.jtouzy.cv.model.utils.ChampionshipCalendarGenerator;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.validation.DataValidationException;

@Path("/championships")
public class ChampionshipResource extends BasicResource<Championship, ChampionshipDAO> {
	public ChampionshipResource() {
		super(Championship.class, ChampionshipDAO.class);
	}
	
	@POST
	@Path("/{id}/calculateRankings")
	public void calculateRankings(@PathParam("id") Integer championshipId)
	throws RankingsCalculateException, DataValidationException {
		try {
			ChampionshipDAO dao = getDAO(ChampionshipDAO.class);
			dao.calculateRankings(championshipId);
		} catch (DAOInstantiationException ex) {
			throw new RankingsCalculateException(ex);
		}
	}
	
	@GET
	@Path("/{id}/previewCalendar")
	@Produces(MediaType.TEXT_HTML + ";charset=utf-8")
	public String previewCalendar(@PathParam("id") Integer championshipId, @QueryParam("return") Boolean returnMatchs)
	throws CalendarGenerationException, DAOInstantiationException {
		final StringBuilder text = new StringBuilder();
		text.append("<html><head><title>Prévisualisation</title></head><body>");
		boolean returnMatchsFlag = false;
		if (returnMatchs != null) {
			returnMatchsFlag = returnMatchs;
		}
		List<Match> matchs = ChampionshipCalendarGenerator.listMatchs(getRequestContext().getConnection(), championshipId, returnMatchsFlag);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd-MM-yyyy, HH:mm");
		Map<Integer, List<Match>> matchsByStep = matchs.stream()
				                                       .collect(Collectors.groupingBy(m -> m.getStep()));
		Iterator<Map.Entry<Integer, List<Match>>> it = matchsByStep.entrySet().iterator();
		Map.Entry<Integer, List<Match>> entry;
		Integer step;
		while (it.hasNext()) {
			entry = it.next();
			step = entry.getKey();
			text.append("<strong>")
			    .append("Journée ")
			    .append(step)
			    .append("</strong><br>");
			entry.getValue().forEach(m -> {
				text.append(m.getFirstTeam().getLabel())
				    .append(" - ")
				    .append(m.getSecondTeam().getLabel())
				    .append(" (")
				    .append(m.getDate().format(formatter))
				    .append(", ")
				    .append(m.getFirstTeam().getGym().getLabel())
				    .append(")")
				    .append("<br>");
			});
			if (it.hasNext())
				text.append("<br>");
		}
		text.append("</body></html>");
		return text.toString();
	}
	
	@GET
	@Path("/{id}/rankings")
	public Championship getChampionship(@PathParam("id") Integer championshipId)
	throws QueryException {
		if (championshipId == null)
			throw new BadRequestException("L'identifiant du championnat doit être renseigné");
		try {
			Championship championship = getDAO(ChampionshipDAO.class).getOneWithTeamsAndMatchs(championshipId);
			if (championship == null)
				throw new NotFoundException("Le championnat " + championshipId + " n'existe pas");
			return championship;
		} catch (DAOInstantiationException ex) {
			throw new QueryException(ex);
		}
	}
}
