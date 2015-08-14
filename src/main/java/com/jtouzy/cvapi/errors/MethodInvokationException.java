package com.jtouzy.cvapi.errors;

public class MethodInvokationException extends ProgramException {
	private static final long serialVersionUID = 1L;
	
	public MethodInvokationException(String propertyName, Throwable cause) {
		super("Problème à l'appel de la méthode pour : " + propertyName, cause);
	}
}
