package com.jtouzy.cv.api.errors;

public class APIConfigurationException extends ProgramException {
	private static final long serialVersionUID = 1L;
	
	public APIConfigurationException(Throwable cause) {
		super("Problème dans la configuration de l'API", cause);
	}
}
