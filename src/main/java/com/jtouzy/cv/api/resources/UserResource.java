package com.jtouzy.cv.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.jtouzy.cv.api.errors.LoginException;
import com.jtouzy.cv.model.classes.User;
import com.jtouzy.cv.model.dao.UserDAO;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;

@Path("/user")
public class UserResource extends GenericResource {
	private static final Logger logger = LogManager.getLogger(UserResource.class); 
	
	@GET
	@Path("/login")
	public String login(@QueryParam("mail") String mail, @QueryParam("password") String password)
	throws LoginException {
		try {
			logger.trace("Tentative de connexion d'un utilisateur");
			UserDAO dao = getDAO(UserDAO.class);
			User user = dao.queryUnique(ImmutableMap.of(User.MAIL_FIELD, mail));
			if (user == null) {
				throw new LoginException("Identifiant ou mot de passe incorrect");
			}
			logger.trace("Mail retrouvé : Vérification du mot de passe");
			if (checkPassword(user, password)) {
				logger.trace("Mot de passe valide : Connexion réussie");
				return "OK";
			} else {
				throw new LoginException("Identifiant ou mot de passe incorrect");
			}
		} catch (DAOInstantiationException | QueryException ex) {
			throw new LoginException(ex);
		}
	}
	
	private boolean checkPassword(User user, String password)
	throws LoginException {
		String salt = user.getPassword().substring(0, 64);
		String validHash = user.getPassword().substring(64);
		HashFunction hashFunction = Hashing.sha256();
		HashCode hashCode = hashFunction.newHasher()
				                        .putString(password + salt, Charsets.UTF_8)
				                        .hash();
		return (hashCode.toString().equals(validHash));
	}
}
