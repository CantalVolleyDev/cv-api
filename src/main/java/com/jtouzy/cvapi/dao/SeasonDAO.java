package com.jtouzy.cvapi.dao;

import com.jtouzy.cvapi.errors.ProgramException;
import com.jtouzy.cvapi.model.Season;

/**
 * Implémentation d'un DAO pour le modèle "Season"
 * @author jtouzy
 */
public class SeasonDAO extends AbstractSingleIdentifierDAO<Season> {
	/**
	 * Constructeur du DAO 
	 * @param daoClass Classe modèle gérée par le DAO (Season)
	 * @throws ProgramException Si la validation technique du DAO est incorrecte
	 */
	public SeasonDAO(Class<Season> daoClass)
	throws ProgramException {
		super(daoClass);
	}
}
