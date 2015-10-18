package com.jtouzy.cv.api.resources;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.api.resources.beanview.TeamView;
import com.jtouzy.cv.api.resources.beanview.UserSimpleView;
import com.jtouzy.cv.api.resources.beanview.UserView;
import com.jtouzy.cv.api.resources.params.TeamUpdateInfos;
import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.api.security.Roles;
import com.jtouzy.cv.model.classes.Comment;
import com.jtouzy.cv.model.classes.SeasonTeam;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;
import com.jtouzy.cv.model.classes.User;
import com.jtouzy.cv.model.dao.CommentDAO;
import com.jtouzy.cv.model.dao.GymDAO;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.cv.model.dao.SeasonTeamDAO;
import com.jtouzy.cv.model.dao.SeasonTeamPlayerDAO;
import com.jtouzy.cv.tools.ToolLauncher;
import com.jtouzy.cv.tools.model.ParameterNames;
import com.jtouzy.cv.tools.model.ToolsList;
import com.jtouzy.dao.errors.DAOCrudException;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.validation.DataValidationException;

@Path("/teams")
public class TeamResource extends GenericResource {
	@GET
	@Path("/{id}/updateInfos")
	@RolesAllowed(Roles.CONNECTED)
	public TeamUpdateInfos getUpdateTeamInfos(@PathParam("id") Integer teamId) {
		try {
			SeasonTeam st = findSeasonTeam(teamId);
			TeamUpdateInfos infos = new TeamUpdateInfos();
			infos.setSeasonTeam(st);
			infos.setGyms(getDAO(GymDAO.class).getAll());
			return infos;
		} catch (DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@PUT
	@Path("/{id}")
	@RolesAllowed(Roles.CONNECTED)
	public void update(@PathParam("id") Integer teamId, SeasonTeam updated)
	throws DataValidationException {
		try {
			findSeasonTeam(teamId);
			List<SeasonTeamPlayer> players = getDAO(SeasonTeamPlayerDAO.class).getAllBySeasonTeam(teamId);
			User user = getRequestContext().getUserPrincipal().getUser();
			if (!user.isAdministrator()) {
				Optional<SeasonTeamPlayer> opt = players.stream()
						                                .filter(stp -> {
						                                	return stp.getPlayer().getIdentifier().equals(user.getIdentifier()); 
						                                })
						                                .findFirst();
				if (!opt.isPresent())
					throw new NotAuthorizedException("Vous n'avez pas les droits de modification de cette équipe", "");
			}
			if (!updated.getIdentifier().equals(teamId)) {
				throw new BadRequestException("Les paramètres de la requête sont incohérents");
			}
			ToolLauncher.build()
			            .target(ToolsList.UPD_TEAM_INFOS)
			            .useConnection(this.getRequestContext().getConnection())
			            .addParameter(ParameterNames.ID, teamId)
			            .addParameter(ParameterNames.DATE, updated.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm")))
			            .addParameter(ParameterNames.GYM, updated.getGym().getIdentifier())
			            .run();
		} catch (DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@GET
	@Path("{id}/details")
	public Response getTeamDetails(@PathParam("id") Integer teamId)
	throws ProgramException {
		try {
			SeasonTeam st = findSeasonTeam(teamId);
			TeamInfos infos = new TeamInfos();
			infos.setSeasonTeam(st);
			infos.setPlayers(getDAO(SeasonTeamPlayerDAO.class).getAllBySeasonTeam(teamId));
			infos.setComments(getDAO(CommentDAO.class).getAllByTeam(teamId));
			infos.setLastMatchs(getDAO(MatchDAO.class).getAllPlayedBySeasonTeam(teamId, 5));
			return buildViewResponse(infos, TeamView.class, UserView.class);
		} catch (DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	private SeasonTeam findSeasonTeam(Integer teamId) {
		if (teamId == null)
			throw new IllegalArgumentException("Le numéro d'équipe est absent");
		try {
			SeasonTeam st = getDAO(SeasonTeamDAO.class).getOneWithDetails(teamId);
			if (st == null)
				throw new NotFoundException("L'équipe " + teamId + " n'existe pas");
			return st;
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