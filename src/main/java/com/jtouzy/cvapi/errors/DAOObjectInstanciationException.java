package com.jtouzy.cvapi.errors;

public class DAOObjectInstanciationException extends ProgramException {
	private static final long serialVersionUID = 1L;

	public DAOObjectInstanciationException(String message) {
		super(message);
	}
	
	public DAOObjectInstanciationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DAOObjectInstanciationException(Throwable cause) {
		super("Probl�me dans l'instanciation d'un objet mod�le", cause);
	}
}
