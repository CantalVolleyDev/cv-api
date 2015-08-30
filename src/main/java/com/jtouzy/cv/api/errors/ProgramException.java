package com.jtouzy.cv.api.errors;

import javax.ws.rs.core.Response;

public class ProgramException extends APIException {
	private static final long serialVersionUID = 1L;
	
	public ProgramException(String message) {
		this(message, null);
	}
	
	public ProgramException(Throwable cause) {
		this(null, cause);
	}
	
	public ProgramException(String message, Throwable cause) {
		super(Response.Status.INTERNAL_SERVER_ERROR, message, cause);
	}
}
