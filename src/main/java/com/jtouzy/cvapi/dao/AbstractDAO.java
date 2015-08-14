package com.jtouzy.cvapi.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.jtouzy.cvapi.Logger;
import com.jtouzy.cvapi.ReflectionInvoker;
import com.jtouzy.cvapi.errors.DAOObjectInstanciationException;
import com.jtouzy.cvapi.errors.InvalidColumnNameException;
import com.jtouzy.cvapi.errors.MethodInvokationException;
import com.jtouzy.cvapi.errors.SQLExecutionException;

/**
 * Implémentation du DAO générique
 * @author jtouzy
 * @param <T> Type d'objet modèle (Bean)
 */
public abstract class AbstractDAO<T> implements DAO<T> {
	/**
	 * Classe de type d'objet modèle
	 * Notamment utilisé pour les instanciations d'objets
	 */
	protected Class<T> daoClass;
	/**
	 * Contexte de la table gérée par le DAO
	 * - Ramenée ici pour optimiser, afin d'éviter d'aller toujours chercher le contexte dans DAOManager -
	 */
	protected TableContext tableContext;
	/**
	 * Cache du DAO
	 * TODO
	 */
	protected DAOCache<T> cache;
	/**
	 * Connection SGBD pour le DAO
	 * TODO
	 */
	protected Connection connection;
	
	/**
	 * Constructeur du DAO générique
	 * Recherche du TableContexte et initialisation du cache du DAO
	 * @param daoClass Classe modèle gérée par le DAO
	 */
	public AbstractDAO(Class<T> daoClass) {
		this.daoClass = daoClass;
		this.tableContext = DAOManager.get().getTableContext(daoClass);
		this.cache = new DAOCache<T>(daoClass);
		
		// Temporaire
		try {
			this.connection = DriverManager.getConnection("jdbc:postgresql://5.135.146.110:5432/jto_cvapi_dvt", "upublic", "jtogri%010811sqlpublic");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T create(T object) {
		// TODO
		return object;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T update(T object) {
		// TODO
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(T object) {
		// TODO
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T createOrUpdate(T object) {
		// TODO
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T createFromValues(Map<String, Object> values)
	throws DAOObjectInstanciationException {
		T object = newObject();
		Iterator<String> it = values.keySet().iterator();
		String propertyName;
		Field field;
		while (it.hasNext()) {
			propertyName = it.next();
			field = tableContext.getFieldForProperty(propertyName);
			if (field == null) {
				throw new DAOObjectInstanciationException(new InvalidColumnNameException(propertyName, tableContext.getTableName()));
			}
			try {
				ReflectionInvoker.invokeSetter(field.getName(), object, values.get(propertyName));
			} catch (MethodInvokationException ex) {
				throw new DAOObjectInstanciationException(ex);
			}
		}
		return object;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> queryAll()
	throws SQLExecutionException, DAOObjectInstanciationException {
		return createObjectsFromQuery(getSQLClauseColumnsWithTable());
	}
	
	/**
	 * Permet de récupérer la clause SQL présente avant le "FROM"
	 * Concaténation du mot-clé SELECT + des colonnes de la table géree par le DAO
	 * @return Chaîne contenant la clause SELECT pour la table gérée par le DAO
	 */
	protected StringBuilder getSQLClauseColumns() {
		return new StringBuilder("select ")
               .append(tableContext.getColumns()
                                   .stream()
								   .collect(Collectors.joining(",")));
	}
	
	/**
	 * Permet de récupérer la clause SQL présente jusqu'au "FROM" inclus<br>
	 * Voir : {@link #getSQLClauseColumns()}<br>
	 * Concaténation de cette méthode ainsi que de la clause "FROM"
	 * @return Chaîne contenant la clause SELECT + FROM pour la table gérée par le DAO
	 */
	protected StringBuilder getSQLClauseColumnsWithTable() {
		return getSQLClauseColumns()
               .append(" from ")
               .append(tableContext.getTableName());
	}
	
	/**
	 * Création d'une liste d'objets du type <code>T</code> en fonction d'une requête SQL.<br>
	 * Le select à exécuter doit ramener assez d'informations pour pouvoir créer des objets du type <code>T</code>.<br>
	 * @param sql Chaîne SQL à exécuter pour ensuite créer les objets à partir du résultat
	 * @return Liste d'objets modèle créés à partir de la requête SQL
	 * @throws SQLExecutionException Si l'exécution de la requête SQL tombe en erreur
	 * @throws DAOObjectInstanciationException Si l'objet ne parvient pas à être instancié
	 */
	protected List<T> createObjectsFromQuery(StringBuilder sql)
	throws SQLExecutionException, DAOObjectInstanciationException {
		try {
			Set<String> columns = tableContext.getColumns();
			ResultSet rs = executeQuery(sql.toString());
			Iterator<String> it;
			String column;
			Map<String,Object> values;
			T object;
			List<T> objects = new ArrayList<>();
			while (rs.next()) {
				values = new HashMap<>();
				it = columns.iterator();
				while (it.hasNext()) {
					column = it.next();
					values.put(column, rs.getObject(column));
				}
				object = createFromValues(values);
				objects.add(object);
			}
			return objects;
		} catch (SQLException ex) {
			throw new SQLExecutionException(ex);
		}
	}
	
	/**
	 * Exécution d'une requête SQL
	 * @param sql Requête SQL à exécuter
	 * @return ResultSet correspondant au résultat de la requête
	 * @throws SQLExecutionException Si l'exécution de la requête SQL tombe en erreur
	 */
	protected ResultSet executeQuery(String sql)
	throws SQLExecutionException {
		try {
			Statement stmt = this.connection.createStatement();
			Logger.log("Exécution du SQL : " + sql);
			return stmt.executeQuery(sql);
		} catch (SQLException ex) {
			throw new SQLExecutionException(ex);
		}
	}

	/**
	 * Permet d'instancier un nouvel objet du type <code>T</code>.<br>
	 * @return Objet créé à partir du constructeur classique
	 * @throws DAOObjectInstanciationException Si l'objet ne parvient pas à être instancié
	 */
	protected T newObject()
	throws DAOObjectInstanciationException {
		try {
			return daoClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new DAOObjectInstanciationException(e);
		}
	}
}
