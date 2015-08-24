package com.jtouzy.cv.api;

import javax.servlet.ServletException;

import org.glassfish.jersey.servlet.ServletContainer;

import com.jtouzy.dao.DAOManager;
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
			DAOManager.init("com.jtouzy.cv.model.classes");
		} catch (ModelClassDefinitionException ex) {
			throw new ServletException(ex);
		}
	}
}
