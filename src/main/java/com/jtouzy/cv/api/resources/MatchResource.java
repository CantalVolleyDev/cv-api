package com.jtouzy.cv.api.resources;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.api.resources.beanview.MatchTeamView;
import com.jtouzy.cv.api.resources.beanview.UserSimpleView;
import com.jtouzy.cv.api.resources.params.MatchUpdateInfos;
import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.api.security.Roles;
import com.jtouzy.cv.api.security.TokenHelper;
import com.jtouzy.cv.model.classes.Comment;
import com.jtouzy.cv.model.classes.Gym;
import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.classes.MatchPlayer;
import com.jtouzy.cv.model.classes.SeasonTeam;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;
import com.jtouzy.cv.model.classes.User;
import com.jtouzy.cv.model.dao.ChampionshipDAO;
import com.jtouzy.cv.model.dao.CommentDAO;
import com.jtouzy.cv.model.dao.GymDAO;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.cv.model.dao.MatchPlayerDAO;
import com.jtouzy.cv.model.dao.SeasonTeamPlayerDAO;
import com.jtouzy.cv.model.dao.UserDAO;
import com.jtouzy.cv.model.errors.RankingsCalculateException;
import com.jtouzy.cv.tools.mail.MailBuilder;
import com.jtouzy.dao.errors.DAOCrudException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.validation.DataValidationException;

@Path("/matchs")
public class MatchResource extends BasicResource<Match, MatchDAO> {
	public MatchResource() {
		super(MatchDAO.class);
	}

	@GET
	@Path("/{id}/updateInfos")
	public MatchUpdateInfos getMatch(@PathParam("id") Integer matchId) {
		try {
			// Lecture du match avec détails (Exception si le match n'existe pas)
			Match match = controlMatchDetails(matchId);
			if (match.getState() == Match.State.V) {
				throw new NotAuthorizedException("Match " + matchId + " déjà validé", "");
			}
			// Select unique pour récupérer tous les joueurs des 2 équipes
			// On fait ensuite un tri pour diviser, à la place de faire 2 select
			List<SeasonTeamPlayer> allPlayers = 
					getDAO(SeasonTeamPlayerDAO.class).getAllBySeasonTeamIn(
							Arrays.asList(match.getFirstTeam().getIdentifier(), 
									      match.getSecondTeam().getIdentifier()));
			// On contrôle que l'un des joueurs d'une des deux équipes soit le joueur connecté
			final Integer connectedId = getCurrentSubmitUser().getIdentifier();
			List<SeasonTeamPlayer> connectedPlayer = allPlayers.stream()
					                                           .filter(s -> s.getPlayer().getIdentifier().equals(connectedId))
					                                           .collect(Collectors.toList());
			if (connectedPlayer.size() == 0) {
				throw new NotAuthorizedException("Impossible de visualiser les données de ce match : Vous ne faites parti d'aucune des deux équipes", "");
			}
			MatchUpdateInfos updateInfos = new MatchUpdateInfos();
			updateInfos.setMatch(match);
			updateInfos.setGyms(getDAO(GymDAO.class).getAll());
			return updateInfos;
		} catch (QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@GET
	@Path("/{id}/details")
	public Response getMatchDetails(@PathParam("id") Integer matchId)
	throws ProgramException, NotFoundException {
		try {
			Match match = controlMatchDetails(matchId);
			MatchDetails details = new MatchDetails();
			details.setMatch(match);
			addMatchPlayers(details, match);
			details.setComments(getDAO(CommentDAO.class).getAllByMatch(matchId));
			return buildViewResponse(details, UserSimpleView.class, MatchTeamView.class);
		} catch (QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@GET
	@Path("/{id}/comments")
	public Response getMatchComments(@PathParam("id") Integer matchId)
	throws ProgramException {
		try {
			return buildViewResponse(getDAO(CommentDAO.class).getAllByMatch(matchId), UserSimpleView.class);
		} catch (QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@PUT
	@Path("/{id}/updateInfos")
	@RolesAllowed(Roles.CONNECTED)
	public void updateInfos(@PathParam("id") Integer matchId, Match infos)
	throws DataValidationException {
		try {
			// Lecture du match avec détails (Exception si le match n'existe pas)
			Match match = controlMatchDetails(matchId);
			if (match.getState() == Match.State.V) {
				throw new NotAuthorizedException("Match " + matchId + " déjà validé", "");
			}
			// Select unique pour récupérer tous les joueurs des 2 équipes
			// On fait ensuite un tri pour diviser, à la place de faire 2 select
			List<SeasonTeamPlayer> allPlayers = 
					getDAO(SeasonTeamPlayerDAO.class).getAllBySeasonTeamIn(
							Arrays.asList(match.getFirstTeam().getIdentifier(), 
									      match.getSecondTeam().getIdentifier()));
			// On contrôle que l'un des joueurs d'une des deux équipes soit le joueur connecté
			final Integer connectedId = getCurrentSubmitUser().getIdentifier();
			List<SeasonTeamPlayer> connectedPlayer = allPlayers.stream()
					                                           .filter(s -> s.getPlayer().getIdentifier().equals(connectedId))
					                                           .collect(Collectors.toList());
			if (connectedPlayer.size() == 0) {
				throw new NotAuthorizedException("Impossible de modifier les données de ce match : Vous ne faites parti d'aucune des deux équipes", "");
			}
			
			LocalDateTime oldDate = match.getDate();
			Gym oldGym = match.getGym();
			match.setDate(infos.getDate());
			match.setGym(infos.getGym());
			getDAO().update(match);
			SeasonTeamPlayer stp = connectedPlayer.get(0);
			List<SeasonTeamPlayer> oppositeManager = allPlayers.stream()
			          										   .filter(s -> !s.getTeam().getIdentifier().equals(stp.getTeam().getIdentifier()) && s.getManager())
			          										   .collect(Collectors.toList());
			if (oppositeManager.size() > 0) {
				MailBuilder.sendMailMatchInfosChanged(match, oldDate, oldGym, oppositeManager.get(0).getPlayer().getMail());
			}
		} catch (QueryException | DAOCrudException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@GET
	@Path("/{id}/submitInfos")
	@RolesAllowed(Roles.CONNECTED)
	public MatchSubmitInfos getMatchSubmitInfos(@PathParam("id") Integer matchId)
	throws ProgramException, NotAuthorizedException, NotFoundException {
		try {
			// Lecture du match avec détails (Exception si le match n'existe pas)
			Match match = controlMatchDetails(matchId);
			if (match.getState() == Match.State.V) {
				throw new NotAuthorizedException("Match " + matchId + " déjà validé", "");
			}
			// Select unique pour récupérer tous les joueurs des 2 équipes
			// On fait ensuite un tri pour diviser, à la place de faire 2 select
			List<SeasonTeamPlayer> allPlayers = 
					getDAO(SeasonTeamPlayerDAO.class).getAllBySeasonTeamIn(
							Arrays.asList(match.getFirstTeam().getIdentifier(), 
									      match.getSecondTeam().getIdentifier()));
			// On contrôle que l'un des joueurs d'une des deux équipes soit le joueur connecté
			final Integer connectedId = getCurrentSubmitUser().getIdentifier();
			List<SeasonTeamPlayer> connectedPlayer = allPlayers.stream()
					                                           .filter(s -> s.getPlayer().getIdentifier().equals(connectedId))
					                                           .collect(Collectors.toList());
			if (connectedPlayer.size() == 0) {
				throw new NotAuthorizedException("Impossible de visualiser les données de ce match : Vous ne faites parti d'aucune des deux équipes", "");
			}	
			Comment submitterComment = null;
			// On contrôle que l'équipe qui a soumis le score ne peut pas revenir le soumettre
			if ((match.getState() == Match.State.S || match.getState() == Match.State.R) && connectedPlayer.size() == 1) {
				Integer connectedPlayerTeam = connectedPlayer.get(0).getTeam().getIdentifier();
				if (match.getScoreSettingTeam().getIdentifier().equals(connectedPlayerTeam)) {
					String teamLabel = match.getFirstTeam().getLabel();
					if (match.getSecondTeam().getIdentifier().equals(match.getScoreSettingTeam().getIdentifier()))
						teamLabel = match.getSecondTeam().getLabel();
					throw new NotAuthorizedException("Le score du match a déjà été soumis par l'équipe " + teamLabel, "");
				}
				if (match.getState() == Match.State.R) {
					submitterComment = getDAO(CommentDAO.class).getOneByMatchTeam(matchId, connectedPlayerTeam);
				}
			}

			// Création des informations du match
			MatchSubmitInfos infos = new MatchSubmitInfos();
			infos.setMatch(match);
			addMatchPlayers(infos, match);
			infos.setFirstTeamPlayers(allPlayers.stream()
					                            .filter(s -> s.getTeam().getIdentifier().equals(match.getFirstTeam().getIdentifier()))
					                            .collect(Collectors.toList()));
			infos.setSecondTeamPlayers(allPlayers.stream()
					                             .filter(s -> s.getTeam().getIdentifier().equals(match.getSecondTeam().getIdentifier()))
					                             .collect(Collectors.toList()));
			// Affectation des équipes du joueur (la plupart du temps une seule, sauf dans le cas
			// ou le joueur fait parti des 2 équipes du match
			infos.setUserTeams(connectedPlayer.stream()
					                          .map(p -> p.getTeam().getIdentifier())
					                          .collect(Collectors.toList()));
			// Recherche du commentaire uniquement si on est sur un refus
			infos.setSubmitterComment(submitterComment);
			return infos;
		} catch (QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	private void addMatchPlayers(MatchInfos infos, Match match)
	throws QueryException {
		// Recherche des joueurs déjà saisis pour le match
		List<MatchPlayer> matchPlayers = getDAO(MatchPlayerDAO.class).getAllByMatch(match.getIdentifier());
		infos.setFirstTeamMatchPlayers(matchPlayers.stream()
                						           .filter(p -> p.getTeam().getIdentifier().equals(match.getFirstTeam().getIdentifier()))
                						           .collect(Collectors.toList()));
		infos.setSecondTeamMatchPlayers(matchPlayers.stream()
                 									.filter(p -> p.getTeam().getIdentifier().equals(match.getSecondTeam().getIdentifier()))
                 									.collect(Collectors.toList()));
	}
	
	private Match controlMatchDetails(Integer matchId)
	throws ProgramException, NotFoundException {
		try {
			Match match = getDAO().getOneWithDetails(matchId);
			if (match == null) {
				throw new NotFoundException("Match " + matchId + " non trouvé");
			}
			return match;
		} catch (QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@POST
	@Path("/{id}/submit")
	@RolesAllowed(Roles.CONNECTED)
	@SuppressWarnings("resource")
	public Response submit(@PathParam("id") Integer matchId, MatchSubmitInfos submit)
	throws ProgramException, DataValidationException {
		Connection connection = getRequestContext().getConnection();
		try {
			if (submit == null) {
				throw new BadRequestException("Paramètre pour les informations du match absent");
			}
			// Lecture des informations du match pour vérifier la cohérence
			Match match = getDAO().getOne(matchId);
			if (match == null) {
				throw new NotFoundException("Match " + matchId + " non trouvé");
			}
			if (match.getState() == Match.State.V) {
				throw new DataValidationException("Le match " + matchId + " est déjà validé");
			}
			// Match soumis par la requête
			Match submitted = submit.getMatch();
			Integer submitterTeamId = submit.getUserTeams().get(0);
			SeasonTeam submitter = new SeasonTeam();
			submitter.setIdentifier(submitterTeamId);
			// Contrôle de l'état du match
			if (submitted.getState() == Match.State.S || submitted.getState() == Match.State.R) {
				// L'utilisateur connecté fait parti des deux équipes : Validation directe
				if (submit.getUserTeams().size() > 1) {
					submitted.setState(Match.State.V);
				} else {
					submitted.setScoreSettingTeam(submitter);
				}
			}
			// Contrôle pour les joueurs
			List<MatchPlayer> validatePlayers = new ArrayList<>();
			boolean validateSubmitter = false, deleteSubmitter = (match.getState() == Match.State.R);
			if (submitted.getState() == Match.State.S || submitted.getState() == Match.State.R) {
				validateSubmitter = true;
			} else if (submitted.getState() == Match.State.V) {
				if (submit.getUserTeams().size() > 1) {
					validatePlayers.addAll(submit.getFirstTeamMatchPlayers());
					validatePlayers.addAll(submit.getSecondTeamMatchPlayers());
				} else {
					validateSubmitter = true;
				}
			}
			if (validateSubmitter) {
				Integer nbp = null;
				if (submitterTeamId.equals(submitted.getFirstTeam().getIdentifier())) {
					validatePlayers.addAll(submit.getFirstTeamMatchPlayers());
					nbp = submitted.getFirstTeam().getPlayersNumber();
				} else {
					validatePlayers.addAll(submit.getSecondTeamMatchPlayers());
					nbp = submitted.getSecondTeam().getPlayersNumber();
				}
				if (validatePlayers.size() < nbp) {
					throw new DataValidationException("Au moins " + nbp + " joueurs doivent être renseignés");
				}
			}
			Comment submitterComment = submit.getSubmitterComment();
			if (submitterComment != null) {
				submitterComment.setEntity(Comment.Entity.MAT);
				submitterComment.setEntityValue(matchId);
				submitterComment.setTeam(submitter);
				submitterComment.setUser(getCurrentSubmitUser());
				submitterComment.setDate(LocalDateTime.now());
			}
			// Gestion des actions en base en transactionnel
			// -> Validation du match
			// -> Insertion des joueurs pour le match
			// -> Insertion du commentaire de match
			// -> Mise à jour du classement
			getRequestContext().getConnection().setAutoCommit(false);
			getDAO().update(submitted);
			MatchPlayerDAO matchPlayerDao = getDAO(MatchPlayerDAO.class);
			CommentDAO commentDao = getDAO(CommentDAO.class);
			if (deleteSubmitter) {
				matchPlayerDao.delete(matchId, submitterTeamId);
				commentDao.deleteMatchComments(matchId, submitterTeamId);
			}
			if (validateSubmitter) {
				matchPlayerDao.create(validatePlayers);
			}
			if (submitterComment != null) {
				commentDao.create(submitterComment);
			}
			getDAO(ChampionshipDAO.class).updateRankings(submitted);
			getRequestContext().getConnection().commit();
			getRequestContext().getConnection().setAutoCommit(true);
			return Response.status(Response.Status.OK)
						   .cookie(Client.createAuthValidationCookie(requestContext))
					       .build();
		} catch (QueryException | DAOCrudException | SQLException | RankingsCalculateException ex) {
			throw new ProgramException(ex);
		} finally {
			try {
				if (!connection.getAutoCommit()) {
					connection.rollback();
					connection.setAutoCommit(true);
				}
			} catch (SQLException ex) {
				throw new ProgramException(ex);
			}
		}
	}
	
	private User getCurrentSubmitUser()
	throws NotAuthorizedException {
		User user = getRequestContext().getUserPrincipal().getUser();
		// On regarde s'il y a un cookie d'authentification de validation directe
		Cookie tokenForScoreValidation = requestContext.getCookies().get(Client.AUTHENTIFICATION_VALIDATION_COOKIE_NAME);
		if (tokenForScoreValidation != null) {
			String tokenValue = tokenForScoreValidation.getValue();
			if (tokenValue != null) {
				tokenValue = TokenHelper.getUserID(tokenValue);
				try {
					user = getDAO(UserDAO.class).getOneByMail(tokenValue);
				} catch (QueryException ex) {
					throw new NotAuthorizedException("Problème dans l'authentification", "");
				}
			}
		}
		return user;
	}
	
	@POST
	@Path("/{id}/comment")
	public void addComment(@PathParam("id") Integer matchId, Comment newComment)
	throws ProgramException, NotFoundException, DataValidationException, BadRequestException {
		try {
			// Gestion manuelle du message d'erreur pour personnalisation
			// Si on laisse la gestion du RolesAllowed, message "impossible d'accéder à cette page"
			Client client = getRequestContext().getUserPrincipal();
			if (client == null) {
				throw new NotAuthorizedException("Vous devez être connecté pour enregistrer un commentaire", "");
			}
			Match match = getDAO().getOne(matchId);
			if (match == null) {
				throw new NotFoundException("Match " + matchId + " non trouvé");
			}
			if (newComment == null) {
				throw new BadRequestException("Paramètre pour les informations du commentaire absent");
			}
			if (newComment.getContent().isEmpty()) {
				throw new BadRequestException("Le commentaire ne peut pas être vide");
			}
			newComment.setEntity(Comment.Entity.MAT);
			newComment.setEntityValue(matchId);
			newComment.setDate(LocalDateTime.now());
			newComment.setUser(getRequestContext().getUserPrincipal().getUser());
			getDAO(CommentDAO.class).create(newComment);
		} catch (QueryException | DAOCrudException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@Override
	public List<Match> getAll() 
	throws APIException {
		try {
			return getDAO(MatchDAO.class).getAllBySeasonAndUser(getSeasonIDWithParam(), getUserIDWithParam());
		} catch (QueryException ex) {
			throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, ex);
		}
	}
}
