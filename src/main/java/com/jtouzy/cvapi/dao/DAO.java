package com.jtouzy.cvapi.dao;

import java.util.List;
import java.util.Map;

import com.jtouzy.cvapi.errors.DAOObjectInstanciationException;
import com.jtouzy.cvapi.errors.DAOObjectParsingException;
import com.jtouzy.cvapi.errors.NullUniqueIndexException;
import com.jtouzy.cvapi.errors.SQLExecutionException;

/**
 * Interface d'un DAO générique
 * @author Jérémy TOUZY
 * @param <T> Type d'objet modèle (Bean)
 */
public interface DAO<T> {
	/**
	 * Récupération de la classe gérée par le DAO
	 * @return Instance de la classe gérée par le DAO
	 */
	public Class<T> getDAOClass();
	/**
	 * Création d'un nouvel objet sur le SGBD
	 * Pour une meilleure utilisation, les valeurs calculées automatiquement lors de la 
	 * création en base devront être affectées à l'objet de sortie
	 * @param object Nouvel objet a créer en base
	 * @return Objet nouvellement créé
	 * @throws DAOObjectParsingException Si l'objet à insérer ne peut être lu correctement
	 * @throws SQLExecutionException Si l'exécution du statement SQL tombe en erreur
	 */
	public T create(T object) throws DAOObjectParsingException, SQLExecutionException;
	/**
	 * Création d'un nouvel objet avec des valeurs
	 * Les valeurs en entrée sont simplement un mapping colonne/valeur avec le bon type de colonne.
	 * @param values Valeurs à affecter à l'objet
	 * @return Objet nouvellement créé
	 * @throws DAOObjectInstanciationException Si l'objet ne parvient pas à être instancié
	 */
	public T createFromValues(Map<String, Object> values) throws DAOObjectInstanciationException;
	/**
	 * Mise à jour d'un objet sur le SGBD
	 * Pour une meilleure utilisation, les valeurs calculées automatiquement lors de la 
	 * mise à jour en base devront être affectées à l'objet de sortie
	 * @param object Objet à mettre à jour
	 * @return Objet mis à jour
	 * @throws DAOObjectParsingException Si l'objet à modifier ne peut être lu correctement
	 * @throws SQLExecutionException Si l'exécution du statement SQL tombe en erreur
	 * @throws NullUniqueIndexException Si l'index unique obligatoire n'est pas renseigné
	 */
	public T update(T object) throws DAOObjectParsingException, SQLExecutionException, NullUniqueIndexException;
	/**
	 * Suppression d'un objet sur le SGBD
	 * @param object Objet à supprimer
	 * @throws DAOObjectParsingException Si l'objet à supprimer ne peut être lu correctement
	 * @throws SQLExecutionException Si l'exécution du statement SQL tombe en erreur
	 * @throws NullUniqueIndexException Si l'index unique obligatoire n'est pas renseigné
	 */
	public void delete(T object) throws DAOObjectParsingException, SQLExecutionException, NullUniqueIndexException;
	/**
	 * Création ou mise à jour de l'objet sur le SGBD
	 * Si l'objet existe déjà (en comparant les colonnes de l'ID), il sera mis à jour, sinon
	 * il sera créé en base. 
	 * Pour une meilleure utilisation, les valeurs calculées automatiquement lors de la 
	 * mise à jour/création en base devront être affectées à l'objet de sortie
	 * @param object Objet à mettre à jour
	 * @return Objet mis à jour
	 */
	public T createOrUpdate(T object);
	/**
	 * Recherche de tous les éléments
	 * @return Tableau d'objets représentant tous les éléments présents en base
	 * @throws DAOObjectInstanciationException Si l'objet ne parvient pas à être instancié
	 * @throws SQLExecutionException Si l'exécution de la requête SQL tombe en erreur
	 */
	public List<T> queryAll() throws DAOObjectInstanciationException, SQLExecutionException;
	/**
	 * Recherche d'un seul élément grâce à l'index unique
	 * @return Objet géré par le DAO retrouvé grâce à l'index unique
	 * @throws DAOObjectInstanciationException Si l'objet ne parvient pas à être instancié
	 * @throws DAOObjectParsingException Si l'objet à créér avec les valeurs ne peut être lu correctement
	 * @throws SQLExecutionException Si l'exécution de la requête SQL tombe en erreur
	 * @throws NullUniqueIndexException Si l'index unique obligatoire n'est pas renseigné
	 */
	public T queryUnique(Map<String, Object> values) throws DAOObjectInstanciationException, DAOObjectParsingException, SQLExecutionException, NullUniqueIndexException;
}
