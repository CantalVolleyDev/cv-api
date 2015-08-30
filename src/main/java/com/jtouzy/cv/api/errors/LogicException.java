package com.jtouzy.cv.api.errors;

import javax.ws.rs.core.Response;

public class LogicException extends APIException {
	private static final long serialVersionUID = 1L;
	
	public LogicException(Response.Status status, String message, Throwable cause) {
		super(status, message, cause);
	}

	public LogicException(Response.Status status, String message) {
		this(status, message, null);
	}

	public LogicException(Response.Status status, Throwable cause) {
		this(status, null, cause);
	}
}
