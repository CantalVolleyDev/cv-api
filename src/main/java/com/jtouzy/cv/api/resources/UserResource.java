package com.jtouzy.cv.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.errors.LoginException;
import com.jtouzy.cv.api.images.ImageFinder;
import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.api.security.TokenHelper;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;
import com.jtouzy.cv.model.classes.Team;
import com.jtouzy.cv.model.classes.User;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.cv.model.dao.SeasonDAO;
import com.jtouzy.cv.model.dao.SeasonTeamPlayerDAO;
import com.jtouzy.cv.model.dao.UserDAO;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.model.ColumnContextNotFoundException;
import com.jtouzy.dao.errors.model.TableContextNotFoundException;
import com.jtouzy.dao.query.Query;

@Path("/user")
public class UserResource extends GenericResource {
	private static final Logger logger = LogManager.getLogger(UserResource.class); 
	
	@POST
	@Path("/login")
	public Response login(UserLoginParameters logParameters)
	throws LoginException {
		try {
			logger.trace("Tentative de connexion d'un utilisateur");
			checkLoginParamsNotNull(logParameters);
			UserDAO dao = getDAO(UserDAO.class);
			User user = dao.queryUnique(ImmutableMap.of(User.MAIL_FIELD, logParameters.mail));
			if (user == null) {
				throw new LoginException("Identifiant ou mot de passe incorrect");
			}
			logger.trace("Mail retrouvé : Vérification du mot de passe");
			checkPassword(user, logParameters.password);
			logger.trace("Mot de passe valide : Connexion réussie");
			return Response.status(Response.Status.OK)
					       .cookie(createAuthCookie(user))
					       .entity(user)
					       .build();
		} catch (DAOInstantiationException | QueryException ex) {
			throw new LoginException(ex);
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
	public Response logout() {
		return Response.status(Response.Status.OK)
				       .cookie(createAuthCookie())
				       .build();
	}
	
	@GET
	@Path("/account")
	public AccountInfos getAccountInfos() {
		try {
			AccountInfos infos = new AccountInfos();
			Integer currentSeason = getDAO(SeasonDAO.class).getCurrentSeason().getIdentifier();
			Integer currentUser = getRequestContext().getUserPrincipal().getUser().getIdentifier();
			Query<SeasonTeamPlayer> queryTeam = getDAO(SeasonTeamPlayerDAO.class).query();
			queryTeam.context().addDirectJoin(Team.class);
			queryTeam.context().addEqualsCriterion(SeasonTeamPlayer.PLAYER_FIELD, currentUser);
			queryTeam.context().addEqualsCriterion(SeasonTeamPlayer.SEASON_FIELD, currentSeason);
			infos.setTeams(queryTeam.many());
			infos.setMatchs(getDAO(MatchDAO.class).getMatchs(currentSeason, currentUser));
			infos.setUploadImage(ImageFinder.isUserImageExists(currentUser));
			return infos;
		} catch (DAOInstantiationException | TableContextNotFoundException | ColumnContextNotFoundException | QueryException ex) {
			throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, ex);
		}
	}
	
	private void checkLoginParamsNotNull(UserLoginParameters logParameters)
	throws LoginException {
		if (logParameters == null || 
			Strings.isNullOrEmpty(logParameters.mail) ||
			Strings.isNullOrEmpty(logParameters.password))
			throw new LoginException("Informations incomplètes pour traiter la connexion");
	}
	
	private void checkPassword(User user, String password)
	throws LoginException {
		String salt = user.getPassword().substring(0, 64);
		String validHash = user.getPassword().substring(64);
		HashFunction hashFunction = Hashing.sha256();
		HashCode hashCode = hashFunction.newHasher()
				                        .putString(password + salt, Charsets.UTF_8)
				                        .hash();
		if (!hashCode.toString().equals(validHash)) {
			throw new LoginException("Identifiant ou mot de passe incorrect");
		}
	}
	
	private NewCookie createAuthCookie() {
		return createAuthCookie(null);
	}
	
	private NewCookie createAuthCookie(User user) {
		return new NewCookie(Client.AUTHENTIFICATION_COOKIE_NAME, 
				             user == null ? "deleted" : TokenHelper.getUserToken(user), 
				             "/", "", "", 
				             user == null ? 0 : NewCookie.DEFAULT_MAX_AGE, false, true);
	}
}
