package com.jtouzy.cvapi.errors;

public class SQLExecutionException extends ProgramException {
	private static final long serialVersionUID = 1L;

	public SQLExecutionException(Throwable cause) {
		super("Problème dans l'exécution d'une instruction SQL", cause);
	}
}
