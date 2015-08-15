package com.jtouzy.cvapi.errors;

public class DAOObjectClassNotFoundException extends ProgramException {
	private static final long serialVersionUID = 1L;

	public DAOObjectClassNotFoundException(Throwable cause) {
		super("Problème dans la recherche de la classe modèle du DAO", cause);

	}
}
