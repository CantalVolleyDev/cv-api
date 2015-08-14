package com.jtouzy.cvapi.errors;

public class DAOObjectParsingException extends ProgramException {
	private static final long serialVersionUID = 1L;

	public DAOObjectParsingException(Throwable cause) {
		super("Problème dans la lecture de l'objet pour la création");
	}
}
