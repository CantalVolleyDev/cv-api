package com.jtouzy.cvapi.errors;

public class MethodInvokationException extends ProgramException {
	private static final long serialVersionUID = 1L;
	
	public MethodInvokationException(String propertyName, Throwable cause) {
		super("Probl�me � l'appel de la m�thode pour : " + propertyName, cause);
	}
}
