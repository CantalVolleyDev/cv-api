package com.jtouzy.cv.api.errors;

import javax.ws.rs.core.Response;

public class LoginException extends LogicException {
	private static final long serialVersionUID = 1L;

	public LoginException(String message) {
		super(Response.Status.UNAUTHORIZED, message);
	}
	
	public LoginException(Throwable cause) {
		super(Response.Status.UNAUTHORIZED, cause);
	}
}

