package com.jtouzy.cvapi.errors;

public class DAOInstantiationException extends ProgramException {
	private static final long serialVersionUID = 1L;

	public DAOInstantiationException(Throwable cause) {
		super("Problème dans l'instanciation du DAO", cause);
	}
}
