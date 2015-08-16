package com.jtouzy.cv.api.errors;

public class ProgramException extends APIException {
	private static final long serialVersionUID = 1L;
	
	public ProgramException() {
		super();
	}

	public ProgramException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProgramException(String message) {
		super(message);
	}

	public ProgramException(Throwable cause) {
		super(cause);
	}
}
