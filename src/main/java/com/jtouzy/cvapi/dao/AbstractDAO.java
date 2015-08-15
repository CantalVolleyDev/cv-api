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
import com.jtouzy.cvapi.errors.DAOObjectParsingException;
import com.jtouzy.cvapi.errors.InvalidColumnNameException;
import com.jtouzy.cvapi.errors.MethodInvokationException;
import com.jtouzy.cvapi.errors.NullUniqueIndexException;
import com.jtouzy.cvapi.errors.SQLExecutionException;

/**
 * Implémentation du DAO générique
 * @author Jérémy TOUZY
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
		this.tableContext = ModelContext.get().getTableContext(daoClass);
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
	public Class<T> getDAOClass() {
		return this.daoClass;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T create(T object)
	throws DAOObjectParsingException, SQLExecutionException {
		StringBuilder sql = getSQLInsertClause().append(" values ( ");
		Iterator<String> it = tableContext.getColumns().iterator();
		String propertyName;
		Field field;
		Object value;
		while (it.hasNext()) {
			propertyName = it.next();
			field = tableContext.getFieldForProperty(propertyName);
			try {
				value = ReflectionInvoker.invokeGetter(field.getName(), object);
			} catch (MethodInvokationException ex) {
				throw new DAOObjectParsingException(ex);
			}
			appendSqlSyntaxFromType(sql, value);
			if (it.hasNext()) {
				sql.append(",");
			}
		}
		sql.append(" )");
		executeSQL(sql.toString());
		return object;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T update(T object)
	throws DAOObjectParsingException, SQLExecutionException, NullUniqueIndexException {
		StringBuilder sql = getSQLUpdateClause();
		Iterator<String> it = tableContext.getColumns().iterator();
		List<String> idColumns = tableContext.getIDColumns();
		String propertyName;
		Field field;
		Object value;
		while (it.hasNext()) {
			propertyName = it.next();
			if (idColumns.contains(propertyName)) {
				continue;
			}
			field = tableContext.getFieldForProperty(propertyName);
			try {
				value = ReflectionInvoker.invokeGetter(field.getName(), object);
			} catch (MethodInvokationException ex) {
				throw new DAOObjectParsingException(ex);
			}
			sql.append(propertyName).append(" = ");
			appendSqlSyntaxFromType(sql, value);
			if (it.hasNext()) {
				sql.append(", ");
			}
		}
		if (sql.charAt(sql.length()-2) == ',') {
			sql.delete(sql.length()-2, sql.length());
		}
		sql.append(" ").append(getWhereClauseUniqueIndex(object));
		executeSQL(sql.toString());
		return object;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(T object)
	throws DAOObjectParsingException, SQLExecutionException, NullUniqueIndexException {
		StringBuilder sql = getSQLDeleteClause();
		sql.append(" ").append(getWhereClauseUniqueIndex(object));
		executeSQL(sql.toString());
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
	 * {@inheritDoc}
	 */
	@Override
	public T queryUnique(Map<String, Object> values)
	throws DAOObjectInstanciationException, DAOObjectParsingException, SQLExecutionException, NullUniqueIndexException {
		StringBuilder sql = getSQLClauseColumnsWithTable()
					        .append(" ").append(getWhereClauseUniqueIndex(values));
		List<T> result = createObjectsFromQuery(sql);
		if (result.size() == 0)
			return null;
		return result.get(0);
	}

	/**
	 * Récupération de la clause SQL complète pour l'identifiant unique<br>
	 * @param object Objet sur lequel récupérer la clause SQL
	 * @return Clause SQL complète pour l'identifiant unique
	 * @throws DAOObjectParsingException Si l'objet à créér avec les valeurs ne peut être lu correctement
	 * @throws NullUniqueIndexException Si l'index unique obligatoire n'est pas renseigné
	 */
	private StringBuilder getWhereClauseUniqueIndex(T object)
	throws DAOObjectParsingException, NullUniqueIndexException {
		// TODO gérer l'ajout du where ou non ?
		final StringBuilder sql = new StringBuilder("where ");
		Iterator<String> it = tableContext.getIDColumns().iterator();
		String propertyName;
		Field field;
		Object value;
		while (it.hasNext()) {
			propertyName = it.next();
			field = tableContext.getFieldForProperty(propertyName);
			try {
				value = ReflectionInvoker.invokeGetter(field.getName(), object);
			} catch (MethodInvokationException ex) {
				throw new DAOObjectParsingException(ex);
			}
			if (value == null)
				throw new NullUniqueIndexException(propertyName);
			sql.append(propertyName).append(" = ");
			appendSqlSyntaxFromType(sql, value);
			if (it.hasNext()) {
				sql.append(" and ");
			}
		}
		return sql;
	}
	
	/**
	 * Récupération de la clause SQL complète pour l'identifiant unique, à partir de valeurs<br>
	 * Création de l'objet via {@link #createFromValues(Map)} puis appel de {@link #getWhereClauseUniqueIndex(Object)}
	 * @param values Valeurs de l'index unique
	 * @return Clause SQL complète pour l'identifiant unique
	 * @throws DAOObjectInstanciationException Si l'objet ne parvient pas à être instancié
	 * @throws DAOObjectParsingException Si l'objet à créér avec les valeurs ne peut être lu correctement
	 * @throws NullUniqueIndexException Si l'index unique obligatoire n'est pas renseigné
	 */
	private StringBuilder getWhereClauseUniqueIndex(Map<String,Object> values)
	throws DAOObjectParsingException, DAOObjectInstanciationException, NullUniqueIndexException {
		return getWhereClauseUniqueIndex(createFromValues(values));
	}
	
	/**
	 * Ajout automatique des caractères nécessaires en fonction du type de données
	 * @param sql Chaîne SQL ou on la valeur doit être rajoutée
	 * @param value Valeur du getter de l'objet
	 */
	private void appendSqlSyntaxFromType(StringBuilder sql, Object value) {
		// TODO Externaliser dans l'adapteur SGBD
		if (value == null || !(value instanceof String)) {
			sql.append(value);
		} else {
			sql.append("'").append(value).append("'");
		}
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
	 * Permet de récupérer la clause SQL INSERT pour la table gérée.<br>
	 * @return Chaîne contenant la clause INSERT pour la table gérée par le DAO
	 */
	protected StringBuilder getSQLInsertClause() {
		return new StringBuilder("insert into ")
		       .append(tableContext.getTableName())
		       .append(" ( ")
		       .append(tableContext.getColumns()
                                   .stream()
                                   .collect(Collectors.joining(",")))
		       .append(" ) ");
	}
	
	/**
	 * Permet de récupérer la clause SQL UPDATE pour la table gérée.<br>
	 * @return Chaîne contenant la clause UPDATE pour la table gérée par le DAO
	 */
	protected StringBuilder getSQLUpdateClause() {
		return new StringBuilder("update ")
		       .append(tableContext.getTableName())
		       .append(" set ");
	}
	
	/**
	 * Permet de récupérer la clause SQL DELETE pour la table gérée.<br>
	 * @return Chaîne contenant la clause DELETE pour la table gérée par le DAO
	 */
	protected StringBuilder getSQLDeleteClause() {
		return new StringBuilder("delete from ")
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
	 * Exécution d'un statement SQL
	 * @param sql Statement SQL à exécuter
	 * @throws SQLExecutionException Si l'exécution du statement SQL tombe en erreur
	 */
	protected void executeSQL(String sql)
	throws SQLExecutionException {
		try {
			Statement stmt = this.connection.createStatement();
			Logger.log("Exécution du Statement SQL : " + sql);
			stmt.execute(sql);
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
