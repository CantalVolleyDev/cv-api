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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.errors.ProgramException;
import com.jtouzy.cv.api.security.Roles;
import com.jtouzy.cv.model.classes.Comment;
import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.classes.MatchPlayer;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;
import com.jtouzy.cv.model.classes.Team;
import com.jtouzy.cv.model.dao.ChampionshipDAO;
import com.jtouzy.cv.model.dao.CommentDAO;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.cv.model.dao.MatchPlayerDAO;
import com.jtouzy.cv.model.dao.SeasonTeamPlayerDAO;
import com.jtouzy.cv.model.errors.RankingsCalculateException;
import com.jtouzy.cv.model.errors.UserNotFoundException;
import com.jtouzy.dao.errors.DAOCrudException;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.validation.DataValidationException;

@Path("/matchs")
public class MatchResource extends BasicResource<Match, MatchDAO> {
	public MatchResource() {
		super(Match.class, MatchDAO.class);
	}

	@GET
	@Path("/{id}/submitInfos")
	@RolesAllowed(Roles.CONNECTED)
	public MatchSubmitInfos getMatchSubmitInfos(@PathParam("id") Integer matchId)
	throws ProgramException, NotAuthorizedException, NotFoundException {
		try {
			// Lecture du match avec détails (Exception si le match n'existe pas)
			Match match = getDAO().queryOneWithDetails(matchId);
			if (match == null) {
				throw new NotFoundException("Match " + matchId + " non trouvé");
			}
			if (match.getState() == Match.State.V) {
				throw new NotAuthorizedException("Match " + matchId + " déjà validé", "");
			}
			// Select unique pour récupérer tous les joueurs des 2 équipes
			// On fait ensuite un tri pour diviser, à la place de faire 2 select
			List<SeasonTeamPlayer> allPlayers = 
					getDAO(SeasonTeamPlayerDAO.class).getPlayers(
							match.getChampionship().getCompetition().getSeason().getIdentifier(), 
							Arrays.asList(match.getFirstTeam().getIdentifier(), 
									      match.getSecondTeam().getIdentifier()));
			// On contrôle que l'un des joueurs d'une des deux équipes soit le joueur connecté
			List<SeasonTeamPlayer> connectedPlayer = allPlayers.stream()
					                                           .filter(s -> s.getPlayer().getIdentifier() == getRequestContext().getUserPrincipal().getUser().getIdentifier())
					                                           .collect(Collectors.toList());
			if (connectedPlayer.size() == 0) {
				throw new NotAuthorizedException("Impossible de visualiser les données de ce match : Vous ne faites parti d'aucune des deux équipes", "");
			}	
			Comment submitterComment = null;
			// On contrôle que l'équipe qui a soumis le score ne peut pas revenir le soumettre
			if ((match.getState() == Match.State.S || match.getState() == Match.State.R) && connectedPlayer.size() == 1) {
				Integer connectedPlayerTeam = connectedPlayer.get(0).getTeam().getIdentifier();
				if (match.getScoreSettingTeam().getIdentifier() == connectedPlayerTeam) {
					String teamLabel = match.getFirstTeam().getLabel();
					if (match.getSecondTeam().getIdentifier() == match.getScoreSettingTeam().getIdentifier())
						teamLabel = match.getSecondTeam().getLabel();
					throw new NotAuthorizedException("Le score du match a déjà été soumis par l'équipe " + teamLabel, "");
				}
				if (match.getState() == Match.State.R) {
					submitterComment = getDAO(CommentDAO.class).getMatchTeamComment(matchId, connectedPlayerTeam);
				}
			}
			// Recherche des joueurs déjà saisis pour le match
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
			// Affectation des équipes du joueur (la plupart du temps une seule, sauf dans le cas
			// ou le joueur fait parti des 2 équipes du match
			infos.setUserTeams(connectedPlayer.stream()
					                          .map(p -> p.getTeam().getIdentifier())
					                          .collect(Collectors.toList()));
			// Recherche du commentaire uniquement si on est sur un refus
			infos.setSubmitterComment(submitterComment);
			return infos;
		} catch (DAOInstantiationException | QueryException ex) {
			throw new ProgramException(ex);
		}
	}
	
	@POST
	@Path("/{id}/submit")
	@RolesAllowed(Roles.CONNECTED)
	public void submit(@PathParam("id") Integer matchId, MatchSubmitInfos submit)
	throws ProgramException, DataValidationException {
		Connection connection = getRequestContext().getConnection();
		try {
			if (submit == null) {
				throw new BadRequestException("Paramètre pour les informations du match absent");
			}
			// Lecture des informations du match pour vérifier la cohérence
			Match match = getDAO().queryForOne(matchId);
			if (match == null) {
				throw new NotFoundException("Match " + matchId + " non trouvé");
			}
			if (match.getState() == Match.State.V) {
				throw new DataValidationException("Le match " + matchId + " est déjà validé");
			}
			// Match soumis par la requête
			Match submitted = submit.getMatch();
			Integer submitterTeamId = submit.getUserTeams().get(0);
			Team submitter = new Team();
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
			List<MatchPlayer> validatePlayers = new ArrayList<MatchPlayer>();
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
				if (submitterTeamId == submitted.getFirstTeam().getIdentifier()) {
					validatePlayers.addAll(submit.getFirstTeamMatchPlayers());
				} else {
					validatePlayers.addAll(submit.getSecondTeamMatchPlayers());
				}
				if (validatePlayers.size() < 3) {
					throw new DataValidationException("Au moins trois joueurs doivent être renseignés");
				}
				//TODO Contrôle par rapport à l'engagement en 3 ou 4
			}
			Comment submitterComment = submit.getSubmitterComment();
			if (submitterComment != null) {
				submitterComment.setEntity(Comment.Entity.MAT);
				submitterComment.setEntityValue(matchId);
				submitterComment.setTeam(submitter);
				submitterComment.setUser(getRequestContext().getUserPrincipal().getUser());
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
		} catch (DAOInstantiationException | QueryException | DAOCrudException | SQLException | RankingsCalculateException ex) {
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
