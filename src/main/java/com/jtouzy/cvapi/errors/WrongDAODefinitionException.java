package com.jtouzy.cvapi.errors;

public class WrongDAODefinitionException extends ProgramException {
	private static final long serialVersionUID = 1L;

	public WrongDAODefinitionException(String message) {
		super("Définition du DAO incorrecte : " + message);
	}
}
