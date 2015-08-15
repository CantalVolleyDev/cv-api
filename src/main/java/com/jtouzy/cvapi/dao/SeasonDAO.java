package com.jtouzy.cvapi.dao;

import com.jtouzy.cvapi.model.Season;
import com.jtouzy.dao.errors.DAOException;
import com.jtouzy.dao.impl.AbstractSingleIdentifierDAO;

/**
 * Implémentation d'un DAO pour le modèle "Season"
 * @author jtouzy
 */
public class SeasonDAO extends AbstractSingleIdentifierDAO<Season> {
	/**
	 * Constructeur du DAO 
	 * @param daoClass Classe modèle gérée par le DAO (Season)
	 * @throws DAOException Si la validation technique du DAO est incorrecte
	 */
	public SeasonDAO(Class<Season> daoClass)
	throws DAOException {
		super(daoClass);
	}
}
