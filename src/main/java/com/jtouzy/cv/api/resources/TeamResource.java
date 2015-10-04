package com.jtouzy.cv.api.resources;

import java.time.LocalDateTime;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.api.resources.beanview.TeamView;
import com.jtouzy.cv.api.resources.beanview.UserSimpleView;
import com.jtouzy.cv.api.resources.beanview.UserView;
import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.model.classes.Comment;
import com.jtouzy.cv.model.classes.SeasonTeam;
import com.jtouzy.cv.model.dao.CommentDAO;
import com.jtouzy.cv.model.dao.SeasonTeamDAO;
import com.jtouzy.cv.model.dao.SeasonTeamPlayerDAO;
import com.jtouzy.dao.errors.DAOCrudException;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.validation.DataValidationException;

@Path("/teams")
public class TeamResource extends GenericResource {
	@GET
	@Path("{id}/details")
	public Response getTeamDetails(@PathParam("id") Integer teamId)
	throws ProgramException {
		if (teamId == null)
			throw new IllegalArgumentException("Le numéro d'équipe est absent");
		
		try {
			TeamInfos infos = new TeamInfos();
			infos.setSeasonTeam(getDAO(SeasonTeamDAO.class).getOneWithDetails(teamId));
			infos.setPlayers(getDAO(SeasonTeamPlayerDAO.class).getAllBySeasonTeam(teamId));
			infos.setComments(getDAO(CommentDAO.class).getAllByTeam(teamId));
			return buildViewResponse(infos, TeamView.class, UserView.class);
		} catch (DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@GET
	@Path("/{id}/comments")
	public Response getTeamComments(@PathParam("id") Integer teamId)
	throws ProgramException {
		try {
			return buildViewResponse(getDAO(CommentDAO.class).getAllByTeam(teamId), UserSimpleView.class);
		} catch (DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@POST
	@Path("/{id}/comment")
	public void addComment(@PathParam("id") Integer teamId, Comment newComment)
	throws ProgramException, NotFoundException, DataValidationException, BadRequestException {
		try {
			// Gestion manuelle du message d'erreur pour personnalisation
			// Si on laisse la gestion du RolesAllowed, message "impossible d'accéder à cette page"
			Client client = getRequestContext().getUserPrincipal();
			if (client == null) {
				throw new NotAuthorizedException("Vous devez être connecté pour enregistrer un commentaire", "");
			}
			SeasonTeam seasonTeam = getDAO(SeasonTeamDAO.class).getOne(teamId);
			if (seasonTeam == null) {
				throw new NotFoundException("Equipe " + teamId + " non trouvée");
			}
			if (newComment == null) {
				throw new BadRequestException("Paramètre pour les informations du commentaire absent");
			}
			if (newComment.getContent().isEmpty()) {
				throw new BadRequestException("Le commentaire ne peut pas être vide");
			}
			newComment.setEntity(Comment.Entity.EQI);
			newComment.setEntityValue(teamId);
			newComment.setDate(LocalDateTime.now());
			newComment.setUser(getRequestContext().getUserPrincipal().getUser());
			getDAO(CommentDAO.class).create(newComment);
		} catch (QueryException | DAOInstantiationException | DAOCrudException ex) {
			throw new ProgramException(ex);
		}
	}
}
