package com.jtouzy.cvapi.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.jtouzy.cvapi.Logger;
import com.jtouzy.cvapi.dao.annotations.DAOTable;
import com.jtouzy.cvapi.dao.annotations.DAOTableField;
import com.jtouzy.cvapi.dao.annotations.DAOTableIDField;

/**
 * Gestionnaire du modèle des classes/SGBD
 * @author Jérémy TOUZY
 */
public class ModelContext {
	/**
	 * Instance unique du ModelContext<br>
	 * Le contexte du modèle est un Singleton, uniquement une seule instance sera créée
	 */
	private static ModelContext instance;
	/**
	 * Mapping entre les classes modèle et le nom des tables associées sur le SGBD
	 */
	private BiMap<Class<?>, String> classToTableMap;
	/**
	 * Mapping entre les classes modèle et l'objet TableContext associé à la table
	 */
	private Map<Class<?>, TableContext> classToFieldsMap;
	/**
	 * Instance de l'objet Reflections pour la lecture des annotations des classes
	 */
	private Reflections reflections;
	
	/**
	 * Récupération du singleton de ModelContext<br>
	 * Au premier appel, la création du singleton sera effectuée
	 * @return Instance du singleton de ModelContext
	 */
	public static ModelContext get() {
		if (instance == null) {
			instance = new ModelContext();
		}
		return instance;
	}
	/**
	 * Constructeur privé<br>
	 * Initialisation des différents mappings
	 */
	private ModelContext() {
		init();
	}
	/**
	 * Initialisation des tableaux de mappings<br>
	 * Initialisation de l'objet Reflections pour lire les annotations
	 */
	private void init() {
		this.classToTableMap = HashBiMap.create();
		this.classToFieldsMap = new HashMap<>();
		this.reflections = new Reflections("com.jtouzy.cvapi.model", 
                                           new FieldAnnotationsScanner(), 
                						   new TypeAnnotationsScanner(),
                						   new SubTypesScanner());
		findModelData();
	}
	/**
	 * Lecture de la définition des classes modèles<br>
	 * Remplissage des tableaux de mapping et des contextes des tables du SGBD
	 */
	private void findModelData() {
		Set<Class<?>> modelList = reflections.getTypesAnnotatedWith(DAOTable.class);
		modelList.forEach(c -> registerClassData(c));
	}
	/**
	 * Création du TableContext pour une table/classe modèle<br>
	 * @param clazz Classe modèle à analyser
	 */
	private void registerClassData(Class<?> clazz) {
		String tableName = clazz.getAnnotation(DAOTable.class).tableName();
		classToTableMap.put(clazz, tableName);
		final TableContext context = new TableContext(tableName);
		Logger.log("Enregistrement de la classe " + clazz.getCanonicalName() + " pour la table " + tableName);
		classToFieldsMap.put(clazz, context);
		reflections.getFieldsAnnotatedWith(DAOTableField.class)
		           .forEach(f -> context.putField(f, f.getAnnotation(DAOTableField.class).fieldName()));
		reflections.getFieldsAnnotatedWith(DAOTableIDField.class)
		           .forEach(f -> context.putIDField(f, f.getAnnotation(DAOTableIDField.class).autoGenerated()));
	}
	/**
	 * Récupération d'un TableContext pour une classe modèle donnée
	 * @param clazz Classe modèle
	 * @return Contexte d'une table associée à la classe demandée 
	 */
	public TableContext getTableContext(Class<?> clazz) {
		return this.classToFieldsMap.get(clazz);
	}
	/**
	 * Récupération d'un TableContext pour un nom de table donné<br>
	 * Appel à la méthode {@link #getTableContext(Class)} pour la classe associée à la table demandée
	 * @param tableName Nom de la table
	 * @return Contexte d'une table associée au nom demandé
	 */
	public TableContext getTableContext(String tableName) {
		return getTableContext(this.classToTableMap.inverse().get(tableName));
	}
}
