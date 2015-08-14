package com.jtouzy.cvapi.dao;

import java.util.List;
import java.util.Map;

import com.jtouzy.cvapi.errors.DAOObjectInstanciationException;
import com.jtouzy.cvapi.errors.SQLExecutionException;

/**
 * Interface d'un DAO générique
 * @author jtouzy
 * @param <T> Type d'objet modèle (Bean)
 */
public interface DAO<T> {
	/**
	 * Création d'un nouvel objet sur le SGBD
	 * Pour une meilleure utilisation, les valeurs calculées automatiquement lors de la 
	 * création en base devront être affectées à l'objet de sortie
	 * @param object Nouvel objet a créer en base
	 * @return Objet nouvellement créé
	 */
	public T create(T object);
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
	 */
	public T update(T object);
	/**
	 * Suppression d'un objet sur le SGBD
	 * @param object Objet à supprimer
	 */
	public void delete(T object);
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
}
