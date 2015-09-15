package com.jtouzy.cv.api.resources;

import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jtouzy.cv.model.classes.Championship;
import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.dao.ChampionshipDAO;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.cv.model.errors.CalendarGenerationException;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOInstantiationException;

@Path("/championships")
public class ChampionshipResource extends BasicResource<Championship, ChampionshipDAO> {
	public ChampionshipResource() {
		super(Championship.class, ChampionshipDAO.class);
	}
	
	@GET
	@Path("/{id}/previewCalendar")
	@Produces(MediaType.TEXT_HTML + ";charset=utf-8")
	public String previewCalendar(@PathParam("id") Integer championshipId)
	throws CalendarGenerationException, DAOInstantiationException {
		final StringBuilder text = new StringBuilder();
		text.append("<html><head><title>Prévisualisation</title></head><body>");
		MatchDAO dao = DAOManager.getDAO(getRequestContext().getConnection(), MatchDAO.class);
		List<Match> matchs = dao.buildCalendar(championshipId, true);
		
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
				    .append(")")
				    .append("<br>");
			});
			if (it.hasNext())
				text.append("<br>");
		}
		text.append("</body></html>");
		return text.toString();
	}
}
