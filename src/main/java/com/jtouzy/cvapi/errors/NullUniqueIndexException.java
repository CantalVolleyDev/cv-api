package com.jtouzy.cvapi.errors;

public class NullUniqueIndexException extends ProgramException {
	private static final long serialVersionUID = 1L;

	public NullUniqueIndexException(String propertyName) {
		super("Une zone de l'index unique n'est pas renseignée : " + propertyName);
	}
}
