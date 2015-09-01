package com.jtouzy.cv.api;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;

import org.glassfish.jersey.servlet.ServletContainer;

import com.jtouzy.cv.api.config.AppConfig;
import com.jtouzy.cv.api.errors.APIConfigurationException;
import com.jtouzy.cv.api.errors.CacheLoadException;
import com.jtouzy.cv.model.dao.SeasonDAO;
import com.jtouzy.dao.DAOManager;
import com.jtouzy.dao.errors.DAOInstantiationException;
import com.jtouzy.dao.errors.QueryException;
import com.jtouzy.dao.errors.model.ModelClassDefinitionException;

/**
 * Servlet générale de l'API
 * @author Jérémy TOUZY
 */
public class APIServlet extends ServletContainer {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Initialisation de la servlet
	 * {@inheritDoc}
	 * Appel à l'initialisation du DAOManager
	 */
	@Override
	public void init() 
	throws ServletException {
		super.init();
		try {
			AppConfig.init();
			DAOManager.init("com.jtouzy.cv.model.classes");
			loadCachedData();
		} catch (ModelClassDefinitionException | APIConfigurationException ex) {
			throw new ServletException(ex);
		}
	}
	
	/**
	 * Chargement des données à mettre en cache au démarrage du serveur
	 * <ul>
	 * 	<li>Chargement de la saison courante dans le SeasonDAO</li>
	 * </ul>
	 * @throws CacheLoadException lorsqu'une exception survient au chargement du cache
	 */
	private void loadCachedData()
	throws CacheLoadException {
		try (Connection loadDataCacheConnection = AppConfig.getDataSource().getConnection()) {
			SeasonDAO seasonDao = DAOManager.getDAO(loadDataCacheConnection, SeasonDAO.class);
			seasonDao.getCurrentSeason();
		} catch (SQLException | DAOInstantiationException | QueryException ex) {
			throw new CacheLoadException(ex);
		}
	}
}
