package com.jtouzy.cv.api.resources;

import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.images.ImageFinder;
import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.api.security.Roles;
import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;
import com.jtouzy.cv.model.classes.User;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.cv.model.dao.SeasonDAO;
import com.jtouzy.cv.model.dao.SeasonTeamPlayerDAO;
import com.jtouzy.cv.model.dao.UserDAO;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;

@Path("/user")
public class UserResource extends GenericResource {
	private static final Logger logger = LogManager.getLogger(UserResource.class); 
	
	@POST
	@Path("/login")
	public Response login(UserLoginParameters logParameters)
	throws NotAuthorizedException {
		User user = commonLogin(logParameters);
		return Response.status(Response.Status.OK)
			           .cookie(Client.createAuthCookie(user))
			           .entity(user)
			           .build();
	}
	
	@POST
	@Path("/loginForValidation")
	public Response loginForValidation(MatchTeamLoginParameters logParameters)
	throws NotAuthorizedException {
		try {
			User user = commonLogin(logParameters);
			Match match = getDAO(MatchDAO.class).getOneWithDetails(logParameters.getMatchId());
			if (match == null) {
				throw new NotAuthorizedException("Le match " + logParameters.getMatchId() + " n'existe pas", "");
			}
			List<SeasonTeamPlayer> players = getDAO(SeasonTeamPlayerDAO.class).getAllBySeasonAndTeam(match.getChampionship().getCompetition().getSeason().getIdentifier(), logParameters.getTeamId());
			Optional<SeasonTeamPlayer> opt = players.stream().filter(stp -> stp.getPlayer().getIdentifier() == user.getIdentifier()).findFirst();
			if (!opt.isPresent()) {
				throw new NotAuthorizedException("Le joueur ne fait pas parti de l'équipe adverse", "");
			}
			return Response.status(Response.Status.OK)
					       .cookie(Client.createAuthValidationCookie(user))
					       .entity(user)
					       .build();
		} catch (DAOInstantiationException | QueryException ex) {
			throw new NotAuthorizedException(ex, "");
		}
	}
	
	private User commonLogin(UserLoginParameters logParameters)
	throws NotAuthorizedException {
		try {
			logger.trace("Tentative de connexion d'un utilisateur");
			checkLoginParamsNotNull(logParameters);
			User user = getDAO(UserDAO.class).getOneByMail(logParameters.mail);
			if (user == null) {
				throw new NotAuthorizedException("Identifiant ou mot de passe incorrect", "");
			}
			logger.trace("Mail retrouvé : Vérification du mot de passe");
			checkPassword(user, logParameters.password);
			logger.trace("Mot de passe valide : Connexion réussie");
			return user;
		} catch (DAOInstantiationException | QueryException ex) {
			throw new NotAuthorizedException(ex, "");
		}
	}
	
	@POST
	@Path("/retrieve")
	public Response retrieve() {
		Client client = getRequestContext().getUserPrincipal();
		ResponseBuilder rB = Response.status(Response.Status.OK);
		if (client != null) {
			rB.entity(client.getUser());
		}
		return rB.build();
	}
	
	@POST
	@Path("/logout")
	@RolesAllowed(Roles.CONNECTED)
	public Response logout() {
		return Response.status(Response.Status.OK)
				       .cookie(Client.createAuthCookie())
				       .build();
	}
	
	@GET
	@Path("/account")
	@RolesAllowed(Roles.CONNECTED)
	public AccountInfos getAccountInfos() {
		try {
			AccountInfos infos = new AccountInfos();
			Integer currentSeason = getDAO(SeasonDAO.class).getCurrentSeason().getIdentifier();
			Integer currentUser = getRequestContext().getUserPrincipal().getUser().getIdentifier();
			infos.setTeams(getDAO(SeasonTeamPlayerDAO.class).getAllBySeasonAndPlayer(currentSeason, currentUser));
			infos.setMatchs(getDAO(MatchDAO.class).getAllBySeasonAndUser(currentSeason, currentUser));
			infos.setUploadImage(ImageFinder.isUserImageExists(currentUser));
			return infos;
		} catch (DAOInstantiationException | QueryException ex) {
			throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, ex);
		}
	}
	
	private void checkLoginParamsNotNull(UserLoginParameters logParameters)
	throws NotAuthorizedException {
		if (logParameters == null || 
			Strings.isNullOrEmpty(logParameters.mail) ||
			Strings.isNullOrEmpty(logParameters.password))
			throw new NotAuthorizedException("Informations incomplètes pour traiter la connexion", "");
	}
	
	private void checkPassword(User user, String password)
	throws NotAuthorizedException {
		String salt = user.getPassword().substring(0, 64);
		String validHash = user.getPassword().substring(64);
		HashFunction hashFunction = Hashing.sha256();
		HashCode hashCode = hashFunction.newHasher()
				                        .putString(password + salt, Charsets.UTF_8)
				                        .hash();
		if (!hashCode.toString().equals(validHash)) {
			throw new NotAuthorizedException("Identifiant ou mot de passe incorrect", "");
		}
	}
}
