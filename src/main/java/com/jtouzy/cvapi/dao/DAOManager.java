package com.jtouzy.cvapi.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Optional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jtouzy.cvapi.errors.DAOInstantiationException;
import com.jtouzy.cvapi.errors.DAOObjectClassNotFoundException;

/**
 * Gestionnaire des DAO.<br>
 * Cette classe permet de gérer les DAO de l'application.<br>
 * Chaque DAO est instancié une seule fois par connexion SGBD.
 * @author Jérémy TOUZY
 */
public class DAOManager {
	/**
	 * Instance unique du DAOManager<br>
	 * Le DAOManager est un Singleton, uniquement une seule instance sera créée
	 */
	private static DAOManager instance;
	/**
	 * Liste des DAO utilisés<br>
	 * Une liste de DAO est enregistrée pour chaque Connection
	 */
	private Multimap<Connection, DAO<?>> daoList;
	
	/**
	 * Récupération du singleton de DAOManager<br>
	 * Au premier appel, la création du singleton sera effectuée
	 * @return Instance du singleton de DAOManager
	 */
	public static DAOManager get() {
		if (instance == null) {
			instance = new DAOManager();
		}
		return instance;
	}
	/**
	 * Constructeur privé<br>
	 * Lancement de l'initialisation du DAOManager
	 */
	private DAOManager() {
		init();
	}
	/**
	 * Initialisation du gestionnaire<br>
	 * Lancement de l'initialisation du contexte du modèle<br>
	 */
	private void init() {
		ModelContext.get();
		this.daoList = HashMultimap.create();
	}
	/**
	 * Récupération d'un DAO
	 * @param connection Instance de connexion pour le DAO demandé
	 * @param dao Classe du DAO demandé
	 * @return Instance du DAO demandé de la classe <code>dao</code>
	 * @throws DAOInstantiationException Lorsque le DAO ne peut être instancié
	 */
	@SuppressWarnings("unchecked")
	public <D extends DAO<T>,T> D getDAO(Connection connection, Class<D> dao)
	throws DAOInstantiationException {
		try {
			Class<T> daoClass = findDAOObjectClass(dao);
			DAO<T> daoInstance = null;
			if (daoList.containsKey(connection)) {
				Optional<DAO<?>> opt = daoList.get(connection).stream().filter(d -> d.getClass().equals(daoClass)).findFirst();
				if (opt.isPresent()) {
					daoInstance = (DAO<T>)opt.get();
				}
			} 
			if (daoInstance == null) {
				daoInstance = instanciateDAO(dao, daoClass);
			}
			daoList.put(connection, daoInstance);
			return (D)daoInstance;
		} catch (DAOObjectClassNotFoundException ex) {
			throw new DAOInstantiationException(ex);
		}
	}
	/**
	 * Recherche de la classe modèle qu'un DAO donné gère
	 * @param daoClassName : Classe du DAO
	 * @return Classe modèle gérée par le DAO donnée
	 * @throws DAOObjectClassNotFoundException Si la classe modèle gérée ne parvient pas à être trouvée
	 */
	@SuppressWarnings("unchecked")
	private <D extends DAO<T>,T> Class<T> findDAOObjectClass(Class<D> daoClassName)
	throws DAOObjectClassNotFoundException {
		try {
			Class<? super D> dao = daoClassName;
			Class<T> daoClass = null;
			String superClassGenerics;
			while (daoClass == null) {
				superClassGenerics = dao.getGenericSuperclass().getTypeName();
				if (!superClassGenerics.contains("<")) {
					dao = dao.getSuperclass();
				} else {
					daoClass = (Class<T>)Class.forName(superClassGenerics.substring(superClassGenerics.indexOf("<")+1, superClassGenerics.indexOf(">")));
					System.out.println(daoClass.getName());
				}
			}
			return daoClass;
		} catch (ClassNotFoundException ex) {
			throw new DAOObjectClassNotFoundException(ex);
		}
	}
	/**
	 * Instanciation d'une classe DAO
	 * @param dao : Classe du DAO à instancier
	 * @param daoClass : Classe modèle gérée par le DAO
	 * @return Instance du DAO créée
	 * @throws DAOInstantiationException Si le DAO ne parvient pas à être instancié
	 */
	private <D extends DAO<T>,T> D instanciateDAO(Class<D> dao, Class<T> daoClass)
	throws DAOInstantiationException {
		try {
			Constructor<D> cs = dao.getConstructor(daoClass.getClass());
			return cs.newInstance(daoClass);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException ex) {
			throw new DAOInstantiationException(ex);
		}
	}
}
