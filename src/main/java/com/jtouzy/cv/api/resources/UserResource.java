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

import com.google.common.base.Strings;
import com.jtouzy.cv.api.errors.APIException;
import com.jtouzy.cv.api.resources.beanview.UserLoginView;
import com.jtouzy.cv.api.resources.params.ChangePasswordParameters;
import com.jtouzy.cv.api.security.Client;
import com.jtouzy.cv.api.security.Roles;
import com.jtouzy.cv.model.classes.Match;
import com.jtouzy.cv.model.classes.SeasonTeamPlayer;
import com.jtouzy.cv.model.classes.User;
import com.jtouzy.cv.model.dao.MatchDAO;
import com.jtouzy.cv.model.dao.SeasonDAO;
import com.jtouzy.cv.model.dao.SeasonTeamPlayerDAO;
import com.jtouzy.cv.model.dao.UserDAO;
import com.jtouzy.cv.security.UserPassword;
import com.jtouzy.cv.tools.mail.MailBuilder;
import com.jtouzy.dao.errors.DAOCrudException;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.validation.DataValidationException;

@Path("/user")
public class UserResource extends GenericResource {
	private static final Logger logger = LogManager.getLogger(UserResource.class); 
	
	@POST
	@Path("/login")
	public Response login(UserLoginParameters logParameters)
	throws NotAuthorizedException {
		User user = commonLogin(logParameters);
		return getBuilderViewResponse(user, UserLoginView.class).cookie(Client.createAuthCookie(user))
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
			List<SeasonTeamPlayer> players = getDAO(SeasonTeamPlayerDAO.class).getAllBySeasonTeam(logParameters.getTeamId());
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
			UserPassword.checkPassword(user, logParameters.password);
			logger.trace("Mot de passe valide : Connexion réussie");
			return user;
		} catch (DAOInstantiationException | QueryException | SecurityException ex) {
			if (ex instanceof SecurityException)
				throw new NotAuthorizedException(ex.getMessage(), "");
			throw new NotAuthorizedException(ex, "");
		}
	}
	
	@POST
	@Path("/retrieve")
	public Response retrieve() {
		Client client = getRequestContext().getUserPrincipal();
		ResponseBuilder rB = Response.status(Response.Status.OK);
		if (client != null) {
			rB = getBuilderViewResponse(client.getUser(), UserLoginView.class);
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
	
	@POST
	@Path("/changePassword")
	@RolesAllowed(Roles.CONNECTED)
	public void changePassword(ChangePasswordParameters chgPasswordParams) {
		checkChangePasswordParamsNotNull(chgPasswordParams);
		User currentUser = getRequestContext().getUserPrincipal().getUser();
		if (!currentUser.getMail().equals(chgPasswordParams.mail))
			throw new NotAuthorizedException("Impossible de modifier le mot de passe d'un autre utilisateur", "");
		try {
			UserPassword.checkPassword(currentUser, chgPasswordParams.password);
		} catch (SecurityException ex) {
			throw new NotAuthorizedException(ex.getMessage(), "");
		}
		if (!chgPasswordParams.newPassword.equals(chgPasswordParams.newPasswordConfirm))
			throw new NotAuthorizedException("La confirmation du nouveau mot de passe n'est pas conforme", "");
		currentUser.setPassword(UserPassword.getFullHashedNewPassword(chgPasswordParams.newPassword));
		try {
			getDAO(UserDAO.class).update(currentUser);
		} catch (DAOInstantiationException | DAOCrudException | DataValidationException ex) {
			throw new NotAuthorizedException(ex);
		}
		MailBuilder.sendMailNewPassword(currentUser, chgPasswordParams.newPassword);
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
	
	private void checkChangePasswordParamsNotNull(ChangePasswordParameters chgPasswordParameters)
	throws NotAuthorizedException {
		if (chgPasswordParameters == null || 
			Strings.isNullOrEmpty(chgPasswordParameters.mail) ||
			Strings.isNullOrEmpty(chgPasswordParameters.password) ||
			Strings.isNullOrEmpty(chgPasswordParameters.newPassword) ||
			Strings.isNullOrEmpty(chgPasswordParameters.newPasswordConfirm))
			throw new NotAuthorizedException("Informations incomplètes pour traiter le changement de mot de passe", "");
	}
}
