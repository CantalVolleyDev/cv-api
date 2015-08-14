package com.jtouzy.cvapi.errors;

public class InvalidColumnNameException extends ProgramException {
	private static final long serialVersionUID = 1L;

	public InvalidColumnNameException(String column, String table) {
		super("Colonne " + column + " inexistante pour la table " + table);
	}
}
