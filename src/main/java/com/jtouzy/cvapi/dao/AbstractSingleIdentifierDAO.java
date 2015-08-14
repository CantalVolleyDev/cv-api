package com.jtouzy.cvapi.dao;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import com.jtouzy.cvapi.errors.DAOObjectInstanciationException;
import com.jtouzy.cvapi.errors.DAOObjectParsingException;
import com.jtouzy.cvapi.errors.NullUniqueIndexException;
import com.jtouzy.cvapi.errors.SQLExecutionException;
import com.jtouzy.cvapi.errors.WrongDAODefinitionException;

/**
 * Implémentation du DAO générique concernant les tables identifiées par une seule primary KEY
 * @author jtouzy
 * @param <T> Type d'objet modèle (Bean)
 */
public abstract class AbstractSingleIdentifierDAO<T> extends AbstractDAO<T> {
	/**
	 * Constructeur du DAO<br>
	 * Recherche du TableContexte et initialisation du cache du DAO<br>
	 * Validation technique du DAO
	 * @param daoClass Classe modèle gérée par le DAO
	 * @throws WrongDAODefinitionException Si la validation technique du DAO est incorrecte
	 */
	public AbstractSingleIdentifierDAO(Class<T> daoClass)
	throws WrongDAODefinitionException {
		super(daoClass);
		checkValidDao();
	}
	/**
	 * Validation technique du DAO<br>
	 * Le système regarde que la table associée au DAO possède bien uniquement une seule colonne d'index et qu'elle soit numérique<br>
	 * @throws WrongDAODefinitionException Si la validation technique du DAO est incorrecte
	 */
	private void checkValidDao()
	throws WrongDAODefinitionException {
		List<String> columns = tableContext.getIDColumns();
		if (columns.size() != 1)
			throw new WrongDAODefinitionException("La table gérée par le DAO " + this.getClass().getCanonicalName() + " doit posséder uniquement un seul index numérique");
		String column = columns.get(0);
		Field fld = tableContext.getFieldForProperty(column);
		if (fld.getType() != Integer.class) {
			throw new WrongDAODefinitionException("La table gérée par le DAO " + this.getClass().getCanonicalName() + " doit posséder un index numérique");
		}
	}
	/**
	 * Recherche d'un élément en particulier
	 * @param id Identifiant de l'index unique
	 * @return Objet retrouvé à partir de son index unique, ou NULL si inexistant
	 * @throws DAOObjectInstanciationException Si l'objet ne parvient pas à être instancié
	 * @throws DAOObjectParsingException Si l'objet à créér avec les valeurs ne peut être lu correctement
	 * @throws SQLExecutionException Si l'exécution de la requête SQL tombe en erreur
	 * @throws NullUniqueIndexException Si l'index unique obligatoire n'est pas renseigné
	 */
	public T queryForOne(Integer id)
	throws DAOObjectInstanciationException, DAOObjectParsingException, SQLExecutionException, NullUniqueIndexException {
		return queryUnique(ImmutableMap.<String,Object>builder().put(getIDColumn(), id).build());
	}
	/**
	 * Récupération de la colonne de l'index unique<br>
	 * Le système accède directement à la première colonne référencée dans {@link TableContext#getIDColumns()}<br>
	 * Elle est obligatoirement renseignée car contrôlée par la méthode {@link #checkValidDao()}
	 * @return Le nom de la colonne de l'index unique numérique
	 */
	private String getIDColumn() {
		return tableContext.getIDColumns().get(0);
	}
}
