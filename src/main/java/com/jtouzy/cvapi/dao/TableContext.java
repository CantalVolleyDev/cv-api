package com.jtouzy.cvapi.dao;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.jtouzy.cvapi.Logger;

/**
 * Contexte de définition d'une table du SGBD
 * @author jtouzy
 */
public class TableContext {
	/**
	 * Clé contenant chaque Field contenu dans la classe associée à la table.<br>
	 * Pour chaque field, une chaîne représente le nom de sa colonne en base.<br>
	 * Cette <code>BiMap</code> est inversible car le Field et le nom de la colonne sont unique.
	 */
	private BiMap<Field,String> fieldsMapping = HashBiMap.create();
	/**
	 * Table contenant les champs de l'index unique de la table gérée.<br>
	 * Elle contient un booléen associé qui indique si l'index est généré automatiquement ou non.
	 */
	private Map<Field,Boolean> idFields = new HashMap<Field,Boolean>();
	/**
	 * Nom de la table définie dans ce contexte
	 */
	private String tableName;
	
	/**
	 * Constructeur
	 * @param tableName Nom de la table définie dans ce contexte
	 */
	public TableContext(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * Ajout d'un champ associé à cette définition de table SQL.
	 * @param field Instance de l'attribut de la classe pour cette table SQL
	 * @param fieldName Nom du champ de la table SQL
	 */
	public void putField(Field field, String fieldName) {
		Logger.log("Enregistrement du champ " + field.getName() + " avec la définition " + fieldName);
		fieldsMapping.put(field, fieldName);
	}
	/**
	 * Ajout d'un champ de l'index unique à cette définition de table SQL.
	 * @param field Instance de l'attribut de la classe pour cette table SQL
	 * @param autoGenerated Le champ est-il auto-généré ?
	 */
	public void putIDField(Field field, boolean autoGenerated) {
		Logger.log("Définition du champ unique " + field.getName() + " avec la définition " + fieldsMapping.get(field));
		idFields.put(field, autoGenerated);
	}
	/**
	 * Récupération de la liste des colonnes SQL de l'index unique.
	 * @return Tableau contenant la liste des colonnes de l'index unique
	 */
	public List<String> getIDColumns() {
		return idFields.keySet().stream()
				       			.map(f -> fieldsMapping.get(f))
				       			.collect(Collectors.toList());
	}
	/**
	 * Récupération des colonnes de la table SQL.
	 * @return Tableau unique contenant la liste des colonnes de la table
	 */
	public Set<String> getColumns() {
		return fieldsMapping.values();
	}
	/**
	 * Récupération de l'instance du <code>Field</code> pour une colonne SQL donnée.
	 * @param property Nom de la colonne SQL
	 * @return Instance du <code>Field</code> associé à la colonne SQL
	 */
	public Field getFieldForProperty(String property) {
		return fieldsMapping.inverse().get(property);
	}
	/**
	 * Récupération du nom de la table gérée dans ce contexte
	 * @return Nom de table SQL gérée
	 */
	public String getTableName() {
		return tableName;
	}
}